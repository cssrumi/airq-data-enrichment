package pl.airq.enrichment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import pl.airq.common.process.EventParser;
import pl.airq.common.process.ctx.enriched.EnrichedDataCreatedEvent;
import pl.airq.common.process.ctx.enriched.EnrichedDataDeletedEvent;
import pl.airq.common.process.ctx.enriched.EnrichedDataUpdatedEvent;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementCreatedEvent;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementDeletedEvent;
import pl.airq.common.process.ctx.gios.aggragation.GiosMeasurementUpdatedEvent;
import pl.airq.common.process.ctx.gios.command.UpdateGiosMeasurement;
import pl.airq.common.process.ctx.gios.installation.GiosInstallationCreatedEvent;
import pl.airq.common.process.ctx.gios.installation.GiosInstallationDeletedEvent;
import pl.airq.common.process.ctx.gios.installation.GiosInstallationUpdatedEvent;

@Dependent
class EventParserProducer {

    @Produces
    @Singleton
    EventParser eventParser(ObjectMapper objectMapper) {
        final EventParser eventParser = new EventParser(objectMapper);
        eventParser.registerEvents(Set.of(
                GiosMeasurementCreatedEvent.class,
                GiosMeasurementUpdatedEvent.class,
                GiosMeasurementDeletedEvent.class,
                EnrichedDataCreatedEvent.class,
                EnrichedDataUpdatedEvent.class,
                EnrichedDataDeletedEvent.class,
                UpdateGiosMeasurement.class
        ));
        return eventParser;
    }

}
