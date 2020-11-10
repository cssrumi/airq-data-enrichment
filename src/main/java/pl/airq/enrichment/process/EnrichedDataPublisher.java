package pl.airq.enrichment.process;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.domain.PersistentRepository;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.process.EventParser;
import pl.airq.common.process.ctx.enriched.EnrichedDataEventPayload;
import pl.airq.common.process.event.AirqEvent;
import pl.airq.common.store.key.TSKey;

@ApplicationScoped
public class EnrichedDataPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichedDataPublisher.class);

    private final Emitter<String> emitter;
    private final String topic;
    private final EventParser parser;

    @Inject
    public EnrichedDataPublisher(@OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 100)
                                 @Channel("data-enriched") Emitter<String> emitter,
                                 @ConfigProperty(name = "mp.messaging.outgoing.data-enriched.topic") String topic,
                                 EventParser parser) {
        this.emitter = emitter;
        this.topic = topic;
        this.parser = parser;
    }

    public Uni<Void> from(PersistentRepository.Result result, TSKey key, EnrichedData data) {
        switch (result) {
            case SAVED:
                return created(key, data);
            case UPSERTED:
                return updated(key, data);
            default:
                LOGGER.warn("Unhandled result type: {}", result);
                return Uni.createFrom().voidItem();
        }
    }

    public Uni<Void> created(TSKey key, EnrichedData data) {
        return publish(key, EnrichedDataEventFactory.create(data));
    }

    public Uni<Void> updated(TSKey key, EnrichedData data) {
        return publish(key, EnrichedDataEventFactory.update(data));
    }

    public Uni<Void> deleted(TSKey key, EnrichedData data) {
        return publish(key, EnrichedDataEventFactory.delete(data));
    }

    public Uni<Void> deleted(TSKey key) {
        return publish(key, EnrichedDataEventFactory.delete());
    }

    Uni<Void> publish(TSKey key, AirqEvent<EnrichedDataEventPayload> event) {
        LOGGER.info("Publishing... {} - {}", key.value(), event.eventType());
        return Uni.createFrom().item(createMessage(key, event))
                  .onItem().invoke(emitter::send)
                  .onItem().ignore().andContinueWithNull();
    }

    private Message<String> createMessage(TSKey key, AirqEvent<EnrichedDataEventPayload> event) {
        OutgoingKafkaRecordMetadata<TSKey> metadata = OutgoingKafkaRecordMetadata
                .<TSKey>builder()
                .withTopic(topic)
                .withKey(key)
                .build();
        return Message.of(parser.parse(event))
                      .addMetadata(metadata);
    }
}
