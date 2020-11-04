package pl.airq.enrichment.process.gios;

import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.domain.PersistentRepository;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementEventPayload;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;
import pl.airq.enrichment.domain.DataEnricherService;
import pl.airq.enrichment.infrastructure.EnrichedDataRepositoryPostgres;
import pl.airq.enrichment.process.EnrichedDataPublisher;

@ApplicationScoped
class GiosMeasurementCreatedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiosMeasurementCreatedHandler.class);

    private final DataEnricherService dataEnricherService;
    private final EnrichedDataRepositoryPostgres repository;
    private final EnrichedDataPublisher publisher;

    @Inject
    public GiosMeasurementCreatedHandler(DataEnricherService dataEnricherService,
                                         EnrichedDataRepositoryPostgres repository,
                                         EnrichedDataPublisher publisher) {
        this.dataEnricherService = dataEnricherService;
        this.repository = repository;
        this.publisher = publisher;
    }

    Uni<Boolean> handle(TSKey key, AirqEvent<GiosMeasurementEventPayload> event) {
        return Uni.createFrom().item(event.payload.measurement)
                  .onItem().transformToUni(dataEnricherService::enrichGiosMeasurement)
                  .onItem().transformToUni(data -> saveAndPublish(key, data));
    }

    private Uni<Boolean> saveAndPublish(TSKey key, EnrichedData data) {
        return repository.upsert(data)
                         .call(result -> publisher.from(result, key, data))
                         .map(PersistentRepository.Result::isSuccess);
    }
}
