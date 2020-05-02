package pl.airq.process.command;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.time.OffsetDateTime;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.domain.DataProvider;
import pl.airq.domain.data.DataService;
import pl.airq.domain.data.EnrichedData;
import pl.airq.domain.gios.GiosDataService;
import pl.airq.model.Try;
import pl.airq.model.command.EnrichData;
import pl.airq.model.event.DataEnriched;
import pl.airq.model.event.DataEnrichedPayload;
import pl.airq.process.AirqEventBus;

import static pl.airq.domain.DataProvider.GIOS;
import static pl.airq.model.TopicConstant.DATA_ENRICHED_TOPIC;
import static pl.airq.model.TopicConstant.ENRICH_DATA_TOPIC;

@ApplicationScoped
class EnrichDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichDataHandler.class);
    private final DataService dataService;
    private final GiosDataService giosDataService;
    private final AirqEventBus eventBus;

    @Inject
    public EnrichDataHandler(DataService dataService, GiosDataService giosDataService, AirqEventBus eventBus) {
        this.dataService = dataService;
        this.giosDataService = giosDataService;
        this.eventBus = eventBus;
    }

    @ConsumeEvent(ENRICH_DATA_TOPIC)
    Uni<Try> enrichDataHandler(EnrichData command) {
        return Try.raw(Uni.createFrom()
                          .item(command.getPayload().providersToEnrich)
                          .onItem()
                          .produceUni(this::enrichData)
                          .onItem()
                          .produceUni(this::saveAndPublish));
    }

    private Uni<List<EnrichedData>> enrichData(List<DataProvider> providers) {
        Multi<EnrichedData> enrichedDataMulti;
        if (providers.contains(GIOS)) {
            LOGGER.info("EnrichData for GIOS");
            enrichedDataMulti = giosDataService.getMeasurementsSinceLastHour()
                                               .onItem()
                                               .produceMulti(giosMeasurements -> Multi.createFrom()
                                                                                      .iterable(giosMeasurements))
                                               .flatMap(giosMeasurement -> dataService.enrichGiosData(giosMeasurement)
                                                                                      .toMulti());
        } else {
            enrichedDataMulti = Multi.createFrom().empty();
            LOGGER.warn("Unhandled DataProviders: " + providers.stream().filter(provider -> !provider.equals(GIOS)));
        }

        return enrichedDataMulti.collectItems().asList();
    }

    private Uni<Void> saveAndPublish(List<EnrichedData> enrichedData) {
        return Multi.createFrom()
                    .iterable(enrichedData)
                    .flatMap(data -> dataService.save(data).toMulti())
                    .collectItems()
                    .asList()
                    .onItem()
                    .produceUni(ignore -> publishDataEnrichedEvent(enrichedData));
    }

    private Uni<Void> publishDataEnrichedEvent(List<EnrichedData> enrichedData) {
        return Uni.createFrom().item(() -> {
            LOGGER.info("Publishing DataEnriched Event...");
            DataEnrichedPayload payload = new DataEnrichedPayload(enrichedData);
            DataEnriched event = new DataEnriched(OffsetDateTime.now(), payload);
            eventBus.publish(DATA_ENRICHED_TOPIC, event);
            LOGGER.info("DataEnriched Event published.");
            return null;
        });
    }
}
