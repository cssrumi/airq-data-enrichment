package pl.airq.enrichment.process.gios;

import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import pl.airq.common.domain.Removable;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementEventPayload;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;
import pl.airq.enrichment.process.EnrichedDataPublisher;

@ApplicationScoped
class GiosMeasurementDeletedHandler {

    private final Removable<EnrichedData, TSKey> removable;
    private final EnrichedDataPublisher publisher;

    @Inject
    GiosMeasurementDeletedHandler(Removable<EnrichedData, TSKey> removable,
                                  EnrichedDataPublisher publisher) {
        this.removable = removable;
        this.publisher = publisher;
    }

    Uni<Boolean> handle(TSKey key, AirqEvent<GiosMeasurementEventPayload> giosMeasurementEvent) {
        return removable.removeByKey(key)
                        .call(isSuccess -> isSuccess ? publisher.deleted(key) : Uni.createFrom().voidItem());
    }
}
