package pl.airq.enrichment.process.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.process.Payload;

@RegisterForReflection
public class DataEnrichedPayload implements Payload {

    public final List<EnrichedData> enrichedData;

    public DataEnrichedPayload(List<EnrichedData> enrichedData) {
        this.enrichedData = enrichedData;
    }

    @Override
    public String toString() {
        return "DataEnrichedPayload{" +
                "enrichedData=" + enrichedData +
                '}';
    }
}
