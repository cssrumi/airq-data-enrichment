package pl.airq.enrichment.process.gios;

import io.smallrye.mutiny.Uni;
import java.util.Map;
import java.util.function.BiFunction;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementCreatedEvent;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementDeletedEvent;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementEventPayload;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementUpdatedEvent;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;

@ApplicationScoped
public class GiosMeasurementDispatcher {

    private final GiosMeasurementCreatedHandler giosMeasurementCreatedHandler;
    private final GiosMeasurementUpdatedHandler giosMeasurementUpdatedHandler;
    private final GiosMeasurementDeletedHandler giosMeasurementDeletedHandler;
    private Map<Class<? extends AirqEvent<GiosMeasurementEventPayload>>, BiFunction<TSKey, AirqEvent<GiosMeasurementEventPayload>, Uni<Boolean>>> dispatchMap;

    @Inject
    public GiosMeasurementDispatcher(GiosMeasurementCreatedHandler giosMeasurementCreatedHandler,
                                     GiosMeasurementUpdatedHandler giosMeasurementUpdatedHandler,
                                     GiosMeasurementDeletedHandler giosMeasurementDeletedHandler) {
        this.giosMeasurementCreatedHandler = giosMeasurementCreatedHandler;
        this.giosMeasurementUpdatedHandler = giosMeasurementUpdatedHandler;
        this.giosMeasurementDeletedHandler = giosMeasurementDeletedHandler;
        this.dispatchMap = Map.of(
                GiosMeasurementCreatedEvent.class, giosMeasurementCreatedHandler::handle,
                GiosMeasurementUpdatedEvent.class, giosMeasurementUpdatedHandler::handle,
                GiosMeasurementDeletedEvent.class, giosMeasurementDeletedHandler::handle
        );
    }

    public Uni<Boolean> dispatch(TSKey key, AirqEvent<GiosMeasurementEventPayload> giosMeasurementEvent) {
        return dispatchMap.get(giosMeasurementEvent.getClass()).apply(key, giosMeasurementEvent);
    }

}
