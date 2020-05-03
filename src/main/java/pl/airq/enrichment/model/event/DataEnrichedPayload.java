package pl.airq.enrichment.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import pl.airq.enrichment.domain.data.EnrichedData;
import pl.airq.enrichment.model.Payload;

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
