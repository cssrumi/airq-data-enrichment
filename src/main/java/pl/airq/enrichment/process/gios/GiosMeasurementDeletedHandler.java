package pl.airq.enrichment.process.gios;

import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import pl.airq.common.domain.Removable;
import pl.airq.common.domain.gios.GiosMeasurement;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementEventPayload;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;
import pl.airq.enrichment.process.DataEnrichedPublisher;

@ApplicationScoped
class GiosMeasurementDeletedHandler {

    private final Removable<GiosMeasurement, TSKey> removable;
    private final DataEnrichedPublisher publisher;

    @Inject
    GiosMeasurementDeletedHandler(Removable<GiosMeasurement, TSKey> removable,
                                  DataEnrichedPublisher publisher) {
        this.removable = removable;
        this.publisher = publisher;
    }

    Uni<Boolean> handle(TSKey key, AirqEvent<GiosMeasurementEventPayload> giosMeasurementEvent) {
        return removable.removeByKey(key)
                        .call(() -> publisher.deleted(key));
    }
}
