package pl.airq.enrichment.process;

import java.time.OffsetDateTime;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.process.ctx.enriched.EnrichedDataCreatedEvent;
import pl.airq.common.process.ctx.enriched.EnrichedDataDeletedEvent;
import pl.airq.common.process.ctx.enriched.EnrichedDataEventPayload;
import pl.airq.common.process.ctx.enriched.EnrichedDataUpdatedEvent;
import pl.airq.common.process.event.AirqEvent;

class EnrichedDataEventFactory {

    static AirqEvent<EnrichedDataEventPayload> create(EnrichedData data) {
        return new EnrichedDataCreatedEvent(OffsetDateTime.now(), new EnrichedDataEventPayload(data));
    }

    static AirqEvent<EnrichedDataEventPayload> update(EnrichedData data) {
        return new EnrichedDataUpdatedEvent(OffsetDateTime.now(), new EnrichedDataEventPayload(data));
    }

    static AirqEvent<EnrichedDataEventPayload> delete(EnrichedData data) {
        return new EnrichedDataDeletedEvent(OffsetDateTime.now(), new EnrichedDataEventPayload(data));
    }

    static AirqEvent<EnrichedDataEventPayload> delete() {
        return delete(null);
    }
}
