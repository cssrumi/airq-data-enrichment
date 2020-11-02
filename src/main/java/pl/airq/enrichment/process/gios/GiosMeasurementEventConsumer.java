package pl.airq.enrichment.process.gios;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaMessageMetadata;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import pl.airq.common.process.EventParser;
import pl.airq.common.process.MutinyUtils;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementEventPayload;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;

@ApplicationScoped
class GiosMeasurementEventConsumer {

    private final EventParser parser;
    private final GiosMeasurementDispatcher dispatcher;

    @Inject
    GiosMeasurementEventConsumer(EventParser parser, GiosMeasurementDispatcher dispatcher) {
        this.parser = parser;
        this.dispatcher = dispatcher;
    }

    @Incoming("gios-measurement")
    @SuppressWarnings("unchecked")
    Uni<Void> consume(Message<String> message) {
        return Uni.createFrom().item(parser.deserializeDomainEvent(message.getPayload()))
                  .onItem().transformToUni(event -> dispatchAndPublish(getKey(message), event));
    }

    @SuppressWarnings("unchecked")
    private TSKey getKey(Message<String> message) {
        return ((KafkaMessageMetadata<TSKey>) message.unwrap(KafkaMessageMetadata.class)).getKey();
    }

    private Uni<Void> dispatchAndPublish(TSKey key, AirqEvent<GiosMeasurementEventPayload> event) {
        return dispatcher.dispatch(key, event)
                         .onItem().transformToUni(MutinyUtils::ignoreUniResult);
    }

}
