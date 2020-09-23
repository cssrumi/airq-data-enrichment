package pl.airq.enrichment.process.command;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.domain.DataProvider;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.process.AppEventBus;
import pl.airq.common.process.Try;
import pl.airq.enrichment.domain.data.DataService;
import pl.airq.enrichment.domain.gios.GiosDataService;
import pl.airq.enrichment.model.command.EnrichData;
import pl.airq.enrichment.model.event.DataEnriched;
import pl.airq.enrichment.model.event.DataEnrichedPayload;

import static pl.airq.common.domain.DataProvider.GIOS;
import static pl.airq.enrichment.model.TopicConstant.ENRICH_DATA_TOPIC;

@ApplicationScoped
class EnrichDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichDataHandler.class);
    private final DataService dataService;
    private final GiosDataService giosDataService;
    private final AppEventBus eventBus;

    @Inject
    public EnrichDataHandler(DataService dataService, GiosDataService giosDataService,
                             AppEventBus eventBus) {
        this.dataService = dataService;
        this.giosDataService = giosDataService;
        this.eventBus = eventBus;
    }

    @ConsumeEvent(ENRICH_DATA_TOPIC)
    Uni<Try> enrichDataHandler(EnrichData command) {
        return Try.raw(Uni.createFrom()
                          .item(command.payload.providersToEnrich)
                          .onItem()
                          .transformToUni(this::enrichData)
                          .onItem()
                          .transformToUni(this::saveAndPublish));
    }

    private Uni<List<EnrichedData>> enrichData(List<DataProvider> providers) {
        Multi<EnrichedData> enrichedDataMulti;
        if (providers.contains(GIOS)) {
            LOGGER.info("EnrichData for GIOS");
            enrichedDataMulti = giosDataService.getMeasurementsSinceLastHour()
                                               .onItem()
                                               .transformToMulti(giosMeasurements -> Multi.createFrom()
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
                    .transformToUni(ignore -> publishDataEnrichedEvent(enrichedData));
    }

    private Uni<Void> publishDataEnrichedEvent(List<EnrichedData> enrichedData) {
        return Uni.createFrom().item(() -> {
            LOGGER.info("Publishing DataEnriched Event...");
            DataEnrichedPayload payload = new DataEnrichedPayload(enrichedData);
            DataEnriched event = new DataEnriched(payload);
            eventBus.publish(event);
            LOGGER.info("DataEnriched Event published.");
            return null;
        });
    }
}
