package pl.airq.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import pl.airq.domain.data.EnrichedData;
import pl.airq.model.Payload;

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
