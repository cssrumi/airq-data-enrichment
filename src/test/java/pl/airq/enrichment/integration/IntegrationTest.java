package pl.airq.enrichment.integration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.airq.common.domain.DataProvider;
import pl.airq.common.domain.PersistentRepository;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.domain.enriched.EnrichedDataQuery;
import pl.airq.common.domain.gios.GiosMeasurement;
import pl.airq.common.domain.station.Station;
import pl.airq.common.kafka.AirqEventDeserializer;
import pl.airq.common.kafka.AirqEventSerializer;
import pl.airq.common.kafka.TSKeyDeserializer;
import pl.airq.common.kafka.TSKeySerializer;
import pl.airq.common.process.EventParser;
import pl.airq.common.process.ctx.enriched.EnrichedDataCreatedEvent;
import pl.airq.common.process.ctx.enriched.EnrichedDataEventPayload;
import pl.airq.common.process.ctx.enriched.EnrichedDataUpdatedEvent;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementCreatedEvent;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementEventPayload;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;
import pl.airq.common.vo.StationId;
import pl.airq.common.vo.StationLocation;
import pl.airq.enrichment.util.WeatherInfoFactory;
import pl.airq.enrichment.weather.WeatherClient;
import pl.airq.enrichment.weather.WeatherInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.airq.enrichment.integration.DBConstant.CREATE_ENRICHED_DATA_TABLE;
import static pl.airq.enrichment.integration.DBConstant.DROP_ENRICHED_DATA_TABLE;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTestResource(PostgresResource.class)
@QuarkusTestResource(KafkaResource.class)
@QuarkusTest
class IntegrationTest {

    private static final StationLocation FIXED_STATION_LOCATION = StationLocation.from(1.0f, 2.0f);
    private final Map<TSKey, AirqEvent<EnrichedDataEventPayload>> eventsMap = new ConcurrentHashMap<>();
    private final List<AirqEvent<EnrichedDataEventPayload>> eventsList = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean shouldConsume = new AtomicBoolean(true);

    @InjectMock
    WeatherClient weatherClient;

    @Inject
    PgPool client;
    @Inject
    PersistentRepository<EnrichedData> repository;
    @Inject
    EnrichedDataQuery query;
    @Inject
    KafkaProducer<TSKey, AirqEvent<GiosMeasurementEventPayload>> producer;
    @Inject
    KafkaConsumer<TSKey, AirqEvent<EnrichedDataEventPayload>> consumer;

    @ConfigProperty(name = "mp.messaging.incoming.gios-measurement.topic")
    String giosTopic;
    @ConfigProperty(name = "mp.messaging.outgoing.data-enriched.topic")
    String dataEnrichedTopic;

    @BeforeAll
    void startConsuming() {
        executor.submit(() -> {
            while (shouldConsume.get()) {
                consumer.poll(Duration.ofMillis(100))
                        .records(dataEnrichedTopic)
                        .forEach(record -> {
                            eventsMap.put(record.key(), record.value());
                            eventsList.add(record.value());
                        });
            }
        });
    }

    @AfterAll
    void stopConsuming() {
        shouldConsume.set(false);
        executor.shutdown();
    }

    @BeforeEach
    void clearEvents() {
        recreateEnrichedDataTable();
        eventsMap.clear();
        eventsList.clear();
    }

    @Test
    void repository() {
        Station station = new Station(StationId.from("Station"), FIXED_STATION_LOCATION);
        WeatherInfo weatherInfo = WeatherInfoFactory.random();
        GiosMeasurement measurement = new GiosMeasurement(OffsetDateTime.now(), station, 1.0f, 2.0f);
        EnrichedData enrichedData = new EnrichedData(
                measurement.timestamp,
                measurement.pm10,
                measurement.pm25,
                weatherInfo.temperature,
                weatherInfo.wind,
                weatherInfo.windDirection,
                weatherInfo.humidity,
                weatherInfo.pressure,
                measurement.station.location.getLon(),
                measurement.station.location.getLat(),
                DataProvider.GIOS,
                measurement.station.id
        );

        EnrichedData result = repository.upsert(enrichedData)
                                        .flatMap(r -> query.findByStationAndTimestamp(enrichedData.station.value(), enrichedData.timestamp))
                                        .await().atMost(Duration.ofSeconds(10));

        assertThat(result).isNotNull();
        System.out.println(result);
    }

    @Test
    void withGiosMeasurementCreated_expectEnrichedDataCreatedEvent() {
        Station station = new Station(StationId.from("Station"), FIXED_STATION_LOCATION);
        final OffsetDateTime measurementTimestamp = currentTimestamp();
        GiosMeasurement measurement = new GiosMeasurement(measurementTimestamp, station, 1.0f, 2.0f);
        GiosMeasurementEventPayload createdPayload = new GiosMeasurementEventPayload(measurement);
        GiosMeasurementCreatedEvent createdEvent = new GiosMeasurementCreatedEvent(OffsetDateTime.now(), createdPayload);

        WeatherInfo weatherInfo = WeatherInfoFactory.random();
        when(weatherClient.getWeatherInfo(any(StationId.class), any(OffsetDateTime.class))).thenReturn(Uni.createFrom().item(weatherInfo));

        TSKey createdKey = sendEvent(createdEvent);
        AirqEvent<EnrichedDataEventPayload> receivedEvent = awaitForEvent(createdKey);

        verifyEnrichedDataCount(1);
        verifyEnrichedDataEvent(receivedEvent, EnrichedDataCreatedEvent.class, weatherInfo, measurement);
        verifyEnrichedDataInDB(weatherInfo, measurement);
    }

    @Test
    void with2GiosMeasurementCreatedWithDifferentPm10Value_expectEnrichedDataCreatedEvent() {
        Station station = new Station(StationId.from("Station"), FIXED_STATION_LOCATION);
        final OffsetDateTime measurementTimestamp = currentTimestamp();
        GiosMeasurement measurement = new GiosMeasurement(measurementTimestamp, station, 1.0f, 2.0f);
        GiosMeasurement measurementWithDifferentPm10 = new GiosMeasurement(measurementTimestamp, station, 11.0f, 2.0f);
        GiosMeasurementEventPayload createdPayload = new GiosMeasurementEventPayload(measurement);
        GiosMeasurementEventPayload createdPayloadWithDifferentPm10 = new GiosMeasurementEventPayload(measurementWithDifferentPm10);
        GiosMeasurementCreatedEvent createdEvent = new GiosMeasurementCreatedEvent(
                OffsetDateTime.now(),
                createdPayload
        );
        GiosMeasurementCreatedEvent createdEventWithDifferentPm10 = new GiosMeasurementCreatedEvent(
                OffsetDateTime.now(),
                createdPayloadWithDifferentPm10
        );

        WeatherInfo weatherInfo = WeatherInfoFactory.random();
        when(weatherClient.getWeatherInfo(any(StationId.class), any(OffsetDateTime.class))).thenReturn(Uni.createFrom().item(weatherInfo));

        TSKey createdKey = sendEvent(createdEvent);
        AirqEvent<EnrichedDataEventPayload> receivedEvent1 = awaitForEvent(createdKey);

        TSKey createdKeyWithDifferentPm10 = sendEvent(createdEventWithDifferentPm10);
        AirqEvent<EnrichedDataEventPayload> receivedEvent2 = awaitForEvent(createdKeyWithDifferentPm10);

        assertThat(createdKey).isEqualTo(createdKeyWithDifferentPm10).isNotNull();
        verifyEnrichedDataCount(1);
        verifyEnrichedDataEvent(receivedEvent1, EnrichedDataCreatedEvent.class, weatherInfo, measurement);
        verifyEnrichedDataEvent(receivedEvent2, EnrichedDataUpdatedEvent.class, weatherInfo, measurementWithDifferentPm10);
        verifyEnrichedDataInDB(weatherInfo, measurementWithDifferentPm10);
    }

    private void recreateEnrichedDataTable() {
        client.query(DROP_ENRICHED_DATA_TABLE).execute()
              .flatMap(r -> client.query(CREATE_ENRICHED_DATA_TABLE).execute())
              .await().atMost(Duration.ofSeconds(5));
    }

    private TSKey sendEvent(AirqEvent<GiosMeasurementEventPayload> event) {
        TSKey key = TSKey.from(event.payload.measurement);
        final Future<RecordMetadata> future = producer.send(new ProducerRecord<>(giosTopic, key, event));
        try {
            future.get(5, TimeUnit.SECONDS);
            return key;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private AirqEvent<EnrichedDataEventPayload> awaitForEvent(TSKey key) {
        await().atMost(Duration.ofSeconds(5)).until(() -> eventsMap.containsKey(key));
        return eventsMap.get(key);
    }

    private void verifyEnrichedDataEvent(AirqEvent<EnrichedDataEventPayload> event, Class<? extends AirqEvent<EnrichedDataEventPayload>> eventClass,
                                         WeatherInfo weatherInfo, GiosMeasurement measurement) {
        assertThat(event).isInstanceOf(eventClass);

        verifyEnrichedData(event.payload.enrichedData, weatherInfo, measurement);
    }

    private void verifyEnrichedDataInDB(WeatherInfo weatherInfo, GiosMeasurement measurement) {
        EnrichedData enrichedData = query.findByStationAndTimestamp(measurement.station.id.value(), measurement.timestamp)
                                         .await().atMost(Duration.ofSeconds(5));

        System.out.println(enrichedData);

        verifyEnrichedData(enrichedData, weatherInfo, measurement);
    }

    private void verifyEnrichedData(EnrichedData enrichedData, WeatherInfo weatherInfo, GiosMeasurement measurement) {
        assertThat(enrichedData.provider).isSameAs(DataProvider.GIOS);
        assertThat(enrichedData.pm10).isEqualTo(measurement.pm10);
        assertThat(enrichedData.pm25).isEqualTo(measurement.pm25);
        assertThat(enrichedData.timestamp.toEpochSecond()).isEqualTo(measurement.timestamp.toEpochSecond());
        assertThat(enrichedData.station).isEqualTo(measurement.station.id);
        assertThat(enrichedData.lon).isEqualTo(measurement.station.location.getLon());
        assertThat(enrichedData.lat).isEqualTo(measurement.station.location.getLat());
        assertThat(enrichedData.humidity).isEqualTo(weatherInfo.humidity);
        assertThat(enrichedData.pressure).isEqualTo(weatherInfo.pressure);
        assertThat(enrichedData.temp).isEqualTo(weatherInfo.temperature);
        assertThat(enrichedData.wind).isEqualTo(weatherInfo.wind);
        assertThat(enrichedData.windDirection).isEqualTo(weatherInfo.windDirection);
    }

    private OffsetDateTime currentTimestamp() {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(OffsetDateTime.now().toEpochSecond()), ZoneOffset.systemDefault());
    }

    private void verifyEnrichedDataCount(int value) {
        Set<EnrichedData> data = query.findAll().await().atMost(Duration.ofSeconds(2));
        System.out.println(data);
        assertThat(data).hasSize(value);
    }

    @Dependent
    static class KafkaConsumerConfiguration {

        @ConfigProperty(name = "kafka.bootstrap.servers")
        String bootstrapServers;
        @ConfigProperty(name = "mp.messaging.outgoing.data-enriched.topic")
        String dataEnrichedTopic;

        @Inject
        EventParser parser;

        @Produces
        KafkaProducer<TSKey, AirqEvent<GiosMeasurementEventPayload>> stringKafkaProducer() {
            Properties properties = new Properties();
            properties.put("bootstrap.servers", bootstrapServers);

            return new KafkaProducer<>(properties, new TSKeySerializer(), new AirqEventSerializer<>(parser));
        }

        @Produces
        KafkaConsumer<TSKey, AirqEvent<EnrichedDataEventPayload>> kafkaConsumer() {
            Properties properties = new Properties();
            properties.put("bootstrap.servers", bootstrapServers);
            properties.put("enable.auto.commit", "true");
            properties.put("group.id", "airq-data-enrichment-int-test");
            properties.put("auto.offset.reset", "earliest");

            KafkaConsumer<TSKey, AirqEvent<EnrichedDataEventPayload>> consumer = new KafkaConsumer<>(properties, new TSKeyDeserializer(), new AirqEventDeserializer<>(parser));
            consumer.subscribe(Collections.singleton(dataEnrichedTopic));
            return consumer;
        }

    }
}
