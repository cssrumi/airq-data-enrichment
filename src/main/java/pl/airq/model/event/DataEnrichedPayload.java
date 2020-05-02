package pl.airq.model.event;

import java.util.List;
import pl.airq.domain.data.EnrichedData;
import pl.airq.model.Payload;

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
