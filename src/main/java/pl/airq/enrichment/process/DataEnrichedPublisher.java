package pl.airq.enrichment.process;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.process.EventParser;
import pl.airq.common.process.MutinyUtils;
import pl.airq.common.process.ctx.enriched.EnrichedDataEventPayload;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;

@ApplicationScoped
public class DataEnrichedPublisher {

    private final Emitter<String> emitter;
    private final EventParser parser;

    @Inject
    public DataEnrichedPublisher(@OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 100)
                          @Channel("data-enriched") Emitter<String> emitter,
                          EventParser parser) {
        this.emitter = emitter;
        this.parser = parser;
    }

    public Uni<Void> created(TSKey key, EnrichedData data) {
        return publish(key, DataEnrichedEventFactory.create(data));
    }

    public Uni<Void> updated(TSKey key, EnrichedData data) {
        return publish(key, DataEnrichedEventFactory.update(data));
    }

    public Uni<Void> deleted(TSKey key, EnrichedData data) {
        return publish(key, DataEnrichedEventFactory.delete(data));
    }

    public Uni<Void> deleted(TSKey key) {
        return publish(key, DataEnrichedEventFactory.delete());
    }

    Uni<Void> publish(TSKey key, AirqEvent<EnrichedDataEventPayload> event) {
        return Uni.createFrom().item(createMessage(key, event))
                  .onItem().invoke(emitter::send)
                  .onItem().transformToUni(MutinyUtils::ignoreUniResult);
    }

    private Message<String> createMessage(TSKey key, AirqEvent<EnrichedDataEventPayload> event) {
        final Message<String> message = Message.of(parser.parse(event));
        OutgoingKafkaRecordMetadata<TSKey> metadata = OutgoingKafkaRecordMetadata
                .<TSKey>builder()
                .withKey(key)
                .build();
        message.addMetadata(metadata);

        return message;
    }
}
