package pl.airq.enrichment.process.gios.update;

import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import pl.airq.common.domain.PersistentRepository;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.domain.enriched.EnrichedDataQuery;
import pl.airq.common.process.ctx.gios.command.UpdateGiosMeasurement;
import pl.airq.common.process.ctx.gios.command.UpdateGiosMeasurementPayload;
import pl.airq.common.store.key.TSKey;
import pl.airq.enrichment.infrastructure.EnrichedDataRepositoryPostgres;
import pl.airq.enrichment.process.EnrichedDataPublisher;

@ApplicationScoped
class UpdateGiosMeasurementHandler {

    private final EnrichedDataRepositoryPostgres repository;
    private final EnrichedDataQuery query;
    private final EnrichedDataPublisher publisher;

    @Inject
    UpdateGiosMeasurementHandler(EnrichedDataRepositoryPostgres repository,
                                 EnrichedDataQuery query,
                                 EnrichedDataPublisher publisher) {
        this.repository = repository;
        this.query = query;
        this.publisher = publisher;
    }

    Uni<Boolean> handle(TSKey key, UpdateGiosMeasurement event) {
        return query.findByStationAndTimestamp(event.payload.station, event.payload.timestamp)
                    .onItem().ifNotNull().transform(enrichedData -> update(enrichedData, event.payload))
                    .onItem().ifNotNull().transformToUni(data -> upsertAndPublish(key, data));
    }

    private EnrichedData update(EnrichedData enrichedData, UpdateGiosMeasurementPayload payload) {
        return new EnrichedData(
                enrichedData.timestamp,
                payload.newPm10,
                enrichedData.pm25,
                enrichedData.temp,
                enrichedData.wind,
                enrichedData.windDirection,
                enrichedData.humidity,
                enrichedData.pressure,
                enrichedData.lon,
                enrichedData.lat,
                enrichedData.provider,
                enrichedData.station
        );
    }

    private Uni<Boolean> upsertAndPublish(TSKey key, EnrichedData data) {
        return repository.upsert(data)
                         .call(result -> publisher.from(result, key, data))
                         .map(PersistentRepository.Result::isSuccess);
    }
}
