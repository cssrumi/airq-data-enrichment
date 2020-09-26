package pl.airq.enrichment.process.event;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import javax.enterprise.context.ApplicationScoped;
import pl.airq.common.process.MutinyUtils;

import static pl.airq.enrichment.process.TopicConstant.DATA_ENRICHED_TOPIC;

@ApplicationScoped
class EventLogger {

    static final String EVENT_CONSUMED_TEMPLATE = "%s event consumed. Event: %s";
    static final Logger LOGGER = LoggerFactory.getLogger(EventLogger.class);

    @ConsumeEvent(DATA_ENRICHED_TOPIC)
    public Uni<Void> consume(DataEnriched event) {
        return MutinyUtils.uniFromRunnable(() ->
                LOGGER.info(String.format(EVENT_CONSUMED_TEMPLATE, event.getClass().getSimpleName(), event.toString())));
    }
}
