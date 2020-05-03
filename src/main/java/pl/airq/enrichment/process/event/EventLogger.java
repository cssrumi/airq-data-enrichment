package pl.airq.enrichment.process.event;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import pl.airq.enrichment.model.event.DataEnriched;

import static pl.airq.enrichment.model.TopicConstant.DATA_ENRICHED_TOPIC;


class EventLogger {

    static final String EVENT_CONSUMED_TEMPLATE = "%s event consumed. Event: %s";
    static final Logger LOGGER = LoggerFactory.getLogger(EventLogger.class);

    @ConsumeEvent(DATA_ENRICHED_TOPIC)
    public Uni<Void> consume(DataEnriched event) {
        return Uni.createFrom().item(() -> {
            LOGGER.info(String.format(EVENT_CONSUMED_TEMPLATE, event.getClass().getSimpleName(), event.toString()));
            return null;
        });
    }
}
