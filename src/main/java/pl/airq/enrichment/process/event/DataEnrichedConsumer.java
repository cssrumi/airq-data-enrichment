package pl.airq.enrichment.process.event;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.domain.enriched.AirqDataEnrichedEvent;
import pl.airq.common.process.EventParser;
import pl.airq.enrichment.model.TopicConstant;
import pl.airq.enrichment.model.event.DataEnriched;

@ApplicationScoped
class DataEnrichedConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataEnrichedConsumer.class);
    private final Emitter<String> externalTopic;
    private final EventParser parser;

    @Inject
    DataEnrichedConsumer(@Channel(TopicConstant.DATA_ENRICHED_EXTERNAL_TOPIC) Emitter<String> externalTopic,
                         EventParser parser) {
        this.externalTopic = externalTopic;
        this.parser = parser;
    }

    @ConsumeEvent(TopicConstant.DATA_ENRICHED_TOPIC)
    Uni<Void> dataEnrichedConsumer(DataEnriched event) {
        return Multi.createFrom()
                    .iterable(AirqDataEnrichedEventFactory.from(event))
                    .onItem().transform(parser::parse)
                    .onItem().invoke(rawEvent -> Multi.createFrom().completionStage(externalTopic.send(rawEvent)))
                    .collectItems().asList()
                    .onItem().transform(List::size)
                    .onItem().transform(size -> {
                        LOGGER.info("{} {} has been passed to External Bus.", size, AirqDataEnrichedEvent.class.getSimpleName());
                        return null;
                    });
    }
}
