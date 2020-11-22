package pl.airq.enrichment.process.gios;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.process.EventParser;
import pl.airq.common.process.MutinyUtils;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementEventPayload;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;

@ApplicationScoped
class GiosMeasurementEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiosMeasurementEventConsumer.class);

    private final EventParser parser;
    private final GiosMeasurementDispatcher dispatcher;

    @Inject
    GiosMeasurementEventConsumer(EventParser parser, GiosMeasurementDispatcher dispatcher) {
        this.parser = parser;
        this.dispatcher = dispatcher;
    }

    @SuppressWarnings("unchecked")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10)
    @Incoming("gios-measurement")
    Uni<Void> consume(Message<String> message) {
        return Uni.createFrom().item(parser.deserializeDomainEvent(message.getPayload()))
                  .invoke(airqEvent -> LOGGER.info("AirqEvent arrived: {}, key: {}", airqEvent, getKey(message).value()))
                  .flatMap(event -> dispatch(getKey(message), event))
                  .onFailure().recoverWithItem(Uni.createFrom().voidItem());
    }

    @SuppressWarnings("unchecked")
    private TSKey getKey(Message<String> message) {
        return ((IncomingKafkaRecord<TSKey, String>) message.unwrap(IncomingKafkaRecord.class)).getKey();
    }

    private Uni<Void> dispatch(TSKey key, AirqEvent<GiosMeasurementEventPayload> event) {
        return dispatcher.dispatch(key, event)
                         .invoke(result -> LOGGER.info("Dispatched: {} - {}. Status: {}", key.value(), event.eventType(), status(result)))
                         .onItem().transformToUni(MutinyUtils::ignoreUniResult);
    }

    private String status(Boolean result) {
        return BooleanUtils.isNotTrue(result) ? "Failure" : "Success";
    }

}
