package pl.airq.enrichment.integration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import pl.airq.common.domain.enriched.AirqDataEnrichedEvent;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.domain.gios.installation.Installation;
import pl.airq.common.domain.gios.installation.InstallationQuery;
import pl.airq.common.process.EventParser;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.enrichment.domain.data.DataService;
import pl.airq.enrichment.domain.data.MockEnrichedDataRepositoryPostgres;
import pl.airq.enrichment.domain.gios.GiosMeasurement;
import pl.airq.enrichment.process.emitter.EnrichDataEmitterPublicProxy;
import pl.airq.enrichment.weather.CurrentWeatherInfo;
import pl.airq.enrichment.weather.CurrentWeatherInfoFactory;
import pl.airq.enrichment.weather.WeatherService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wildfly.common.Assert.assertTrue;
import static pl.airq.enrichment.domain.InstallationFactory.installationWithPm10;
import static pl.airq.enrichment.domain.InstallationFactory.installationWithPm25;

@QuarkusTestResource(KafkaResource.class)
@QuarkusTest
class IntegrationTest {

    @InjectSpy
    private DataService dataService;
    @InjectMock
    private InstallationQuery installationQuery;
    @InjectMock
    private WeatherService weatherService;

    @Inject
    private EnrichDataEmitterPublicProxy enrichDataEmitter;
    @Inject
    private MockEnrichedDataRepositoryPostgres enrichedDataRepository;

    @Inject
    private KafkaConsumer<Void, String> kafkaConsumer;
    @Inject
    private EventParser parser;

    @Test
    void integration_whenEnrichDataEventEmitted_expect() {
        Long id = 1L;
        Float pm10 = 1.0f;
        Float pm25 = 2.5f;
        final Installation pm10Installation = installationWithPm10(id, pm10);
        final Installation pm25Installation = installationWithPm25(id, pm25);
        final Set<Installation> installations = Set.of(pm10Installation, pm25Installation);
        final CurrentWeatherInfo weatherInfo = CurrentWeatherInfoFactory.random();

        when(weatherService.getCurrentWeatherInfoByCoordinates(anyString(), anyString()))
                .thenReturn(Uni.createFrom().item(weatherInfo));
        when(installationQuery.getAllWithPMSinceLastHour())
                .thenReturn(Uni.createFrom().item(installations));
        enrichedDataRepository.setSaveAndUpsertResult(Boolean.TRUE);

        enrichDataEmitter.emitEnrichDataEvent();

        final ConsumerRecord<Void, String> record = kafkaConsumer.poll(Duration.ofMinutes(1)).iterator().next();
        final AirqEvent<?> airqEvent = parser.deserializeDomainEvent(record.value());

        assertTrue(airqEvent instanceof AirqDataEnrichedEvent);
        final EnrichedData result = ((AirqDataEnrichedEvent) airqEvent).payload.enrichedData;
        assertEquals(pm10Installation.name, result.station.getId());
        assertEquals(pm10, result.pm10);
        assertEquals(pm25, result.pm25);
        assertEquals(weatherInfo.temperature, result.temp);
        assertEquals(weatherInfo.wind, result.wind);
        assertEquals(weatherInfo.windDirection, result.windDirection);
        assertEquals(weatherInfo.humidity, result.humidity);
        assertEquals(weatherInfo.pressure, result.pressure);
        verify(dataService, times(1)).enrichGiosData(any());
    }

    @Dependent
    static class KafkaConsumerConfiguration {

        @Produces
        KafkaConsumer<Void, String> kafkaConsumer(@ConfigProperty(name = "kafka.bootstrap.servers") String bootstrapServers,
                                                  @ConfigProperty(name = "mp.messaging.outgoing.data-enriched.topic") String topic) {
            Properties properties = new Properties();
            properties.put("bootstrap.servers", bootstrapServers);
            properties.put("enable.auto.commit", "true");
            properties.put("group.id", "airq-data-enrichment-int-test");
            properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("auto.offset.reset", "earliest");

            KafkaConsumer<Void, String> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Collections.singleton(topic));
            return consumer;
        }

    }
}
