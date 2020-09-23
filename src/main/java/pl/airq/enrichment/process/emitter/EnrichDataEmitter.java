package pl.airq.enrichment.process.emitter;

import io.quarkus.scheduler.Scheduled;
import java.util.Arrays;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.domain.DataProvider;
import pl.airq.common.process.AppEventBus;
import pl.airq.enrichment.model.command.EnrichData;
import pl.airq.enrichment.model.command.EnrichDataPayload;

@ApplicationScoped
class EnrichDataEmitter {

    private final static Logger LOGGER = LoggerFactory.getLogger(EnrichDataEmitter.class);
    private final AppEventBus bus;

    @Inject
    EnrichDataEmitter(AppEventBus bus) {
        this.bus = bus;
    }

    @Scheduled(cron = "{data-enrichment.emitter.cron}")
    void emitEnrichDataEvent() {
        EnrichData enrichData = new EnrichData(new EnrichDataPayload(Arrays.asList(DataProvider.values())));
        bus.sendAndForget(enrichData);
        LOGGER.info(String.format("%s command has been send.\nCommand: %s", EnrichData.class.getSimpleName(), enrichData));
    }
}
