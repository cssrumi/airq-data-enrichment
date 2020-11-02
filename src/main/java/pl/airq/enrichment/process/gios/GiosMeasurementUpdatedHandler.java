package pl.airq.enrichment.process.gios;

import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.domain.enriched.EnrichedDataQuery;
import pl.airq.common.domain.gios.GiosMeasurement;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementEventPayload;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;
import pl.airq.enrichment.domain.DataEnricherService;
import pl.airq.enrichment.infrastructure.EnrichedDataRepositoryPostgres;
import pl.airq.enrichment.process.DataEnrichedPublisher;

@ApplicationScoped
class GiosMeasurementUpdatedHandler {

    private final DataEnricherService dataEnricherService;
    private final EnrichedDataRepositoryPostgres repository;
    private final EnrichedDataQuery query;
    private final DataEnrichedPublisher publisher;

    @Inject
    GiosMeasurementUpdatedHandler(DataEnricherService dataEnricherService,
                                  EnrichedDataRepositoryPostgres repository,
                                  EnrichedDataQuery query,
                                  DataEnrichedPublisher publisher) {
        this.dataEnricherService = dataEnricherService;
        this.repository = repository;
        this.query = query;
        this.publisher = publisher;
    }

    Uni<Boolean> handle(TSKey key, AirqEvent<GiosMeasurementEventPayload> event) {
        return query.findByStationAndTimestamp(event.payload.measurement.station.id.value(), event.payload.measurement.timestamp)
                    .onItem().ifNotNull().transform(enrichedData -> update(enrichedData, event.payload.measurement))
                    .onItem().ifNull().switchTo(() -> dataEnricherService.enrichGiosMeasurement(event.payload.measurement))
                    .onItem().transformToUni(data -> upsertAndPublish(key, data));
    }

    private EnrichedData update(EnrichedData enrichedData, GiosMeasurement measurement) {
        return new EnrichedData(
                enrichedData.timestamp,
                measurement.pm10,
                measurement.pm25,
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
                         .call(() -> publisher.updated(key, data));
    }
}
