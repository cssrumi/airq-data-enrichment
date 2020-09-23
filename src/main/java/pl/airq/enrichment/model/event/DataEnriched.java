package pl.airq.enrichment.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import pl.airq.common.process.event.AppEvent;

import static pl.airq.enrichment.model.TopicConstant.DATA_ENRICHED_TOPIC;

@RegisterForReflection
public class DataEnriched extends AppEvent<DataEnrichedPayload> {

    public DataEnriched(DataEnrichedPayload payload) {
        super(payload);
    }

    @Override
    public String defaultTopic() {
        return DATA_ENRICHED_TOPIC;
    }

    @Override
    public String toString() {
        return "DataEnriched{" +
                "timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
}
