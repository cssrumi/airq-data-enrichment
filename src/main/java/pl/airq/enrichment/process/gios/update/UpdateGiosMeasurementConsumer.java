package pl.airq.enrichment.process.gios.update;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.process.EventParser;
import pl.airq.common.process.ctx.gios.command.UpdateGiosMeasurement;
import pl.airq.common.store.key.TSKey;

import static pl.airq.enrichment.domain.Status.status;

@ApplicationScoped
class UpdateGiosMeasurementConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateGiosMeasurementConsumer.class);

    private final EventParser parser;
    private final UpdateGiosMeasurementHandler handler;

    @Inject
    UpdateGiosMeasurementConsumer(EventParser parser, UpdateGiosMeasurementHandler handler) {
        this.parser = parser;
        this.handler = handler;
    }

    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10)
    @Incoming("update-gios-measurement")
    Uni<Void> consume(Message<String> message) {
        return Uni.createFrom().item(parser.deserializeDomainEvent(message.getPayload()))
                  .onItem().castTo(UpdateGiosMeasurement.class)
                  .invoke(airqEvent -> LOGGER.info("AirqEvent arrived: {}, key: {}", airqEvent, getKey(message).value()))
                  .flatMap(event -> dispatch(getKey(message), event))
                  .onFailure().invoke(error -> LOGGER.error("Error occurred during UpdateGiosMeasurement process: {}", error.getMessage()))
                  .call(() -> Uni.createFrom().completionStage(message.ack()));
    }

    @SuppressWarnings("unchecked")
    private TSKey getKey(Message<String> message) {
        return ((IncomingKafkaRecord<TSKey, String>) message.unwrap(IncomingKafkaRecord.class)).getKey();
    }

    private Uni<Void> dispatch(TSKey key, UpdateGiosMeasurement event) {
        return handler.handle(key, event)
                      .invoke(result -> LOGGER.info("Dispatched: {}. Status: {}", event.eventType(), status(result)))
                      .onItem().ignore().andContinueWithNull();

    }

}
