package pl.airq.enrichment.process.event;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import pl.airq.common.domain.enriched.AirqDataEnrichedEvent;
import pl.airq.common.domain.enriched.AirqDataEnrichedPayload;
import pl.airq.enrichment.model.event.DataEnriched;

class AirqDataEnrichedEventFactory {

    private AirqDataEnrichedEventFactory() {
    }

    static List<AirqDataEnrichedEvent> from(DataEnriched event) {
        if (CollectionUtils.isEmpty(event.payload.enrichedData)) {
            return Collections.emptyList();
        }

        return event.payload.enrichedData.stream().map(enrichedData -> {
            final AirqDataEnrichedPayload payload = new AirqDataEnrichedPayload(enrichedData);
            return new AirqDataEnrichedEvent(OffsetDateTime.now(), payload);
        }).collect(Collectors.toUnmodifiableList());
    }


}
