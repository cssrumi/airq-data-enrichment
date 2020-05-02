package pl.airq.process.emitter;

import io.quarkus.scheduler.Scheduled;
import io.vertx.core.eventbus.EventBus;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.domain.DataProvider;
import pl.airq.model.command.EnrichData;
import pl.airq.model.command.EnrichDataPayload;

import static pl.airq.model.TopicConstant.ENRICH_DATA_TOPIC;

@ApplicationScoped
class EnrichDataEmitter {

    private final static Logger LOGGER = LoggerFactory.getLogger(EnrichDataEmitter.class);
    private final EventBus bus;

    @Inject
    EnrichDataEmitter(EventBus bus) {
        this.bus = bus;
    }

    @Scheduled(cron = "{data-enrichment.emitter.cron}")
    void enrichDataScheduler() {
        EnrichData enrichData = new EnrichData(OffsetDateTime.now(), new EnrichDataPayload(Arrays.asList(DataProvider.values())));
        bus.send(ENRICH_DATA_TOPIC, enrichData);
        LOGGER.info(String.format("%s command has been send.\nCommand: %s", EnrichData.class.getSimpleName(), enrichData));
    }
}
