package pl.airq.enrichment.process.gios;

import io.smallrye.mutiny.Uni;
import java.util.Map;
import java.util.function.BiFunction;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementCreatedEvent;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementDeletedEvent;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementEventPayload;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementUpdatedEvent;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;

@ApplicationScoped
public class GiosMeasurementDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiosMeasurementDispatcher.class);

    private final GiosMeasurementCreatedHandler giosMeasurementCreatedHandler;
    private final GiosMeasurementUpdatedHandler giosMeasurementUpdatedHandler;
    private final GiosMeasurementDeletedHandler giosMeasurementDeletedHandler;
    private final Map<String, BiFunction<TSKey, AirqEvent<GiosMeasurementEventPayload>, Uni<Boolean>>> dispatchMap;

    @Inject
    public GiosMeasurementDispatcher(GiosMeasurementCreatedHandler giosMeasurementCreatedHandler,
                                     GiosMeasurementUpdatedHandler giosMeasurementUpdatedHandler,
                                     GiosMeasurementDeletedHandler giosMeasurementDeletedHandler) {
        this.giosMeasurementCreatedHandler = giosMeasurementCreatedHandler;
        this.giosMeasurementUpdatedHandler = giosMeasurementUpdatedHandler;
        this.giosMeasurementDeletedHandler = giosMeasurementDeletedHandler;
        this.dispatchMap = Map.of(
                GiosMeasurementCreatedEvent.class.getSimpleName(), giosMeasurementCreatedHandler::handle,
                GiosMeasurementUpdatedEvent.class.getSimpleName(), giosMeasurementUpdatedHandler::handle,
                GiosMeasurementDeletedEvent.class.getSimpleName(), giosMeasurementDeletedHandler::handle
        );
    }

    public Uni<Boolean> dispatch(TSKey key, AirqEvent<GiosMeasurementEventPayload> giosMeasurementEvent) {
        return dispatchMap.getOrDefault(giosMeasurementEvent.eventType(), this::defaultHandler)
                          .apply(key, giosMeasurementEvent);
    }

    private Uni<Boolean> defaultHandler(TSKey key, AirqEvent<GiosMeasurementEventPayload> giosMeasurementEvent) {
        return Uni.createFrom().item(Boolean.FALSE)
                  .invoke(() -> LOGGER.warn("Unhandled event {} for key {}", giosMeasurementEvent, key.value()));
    }
}
