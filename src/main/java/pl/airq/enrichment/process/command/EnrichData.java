package pl.airq.enrichment.process.command;

import io.quarkus.runtime.annotations.RegisterForReflection;
import pl.airq.common.process.command.AppCommand;

import static pl.airq.enrichment.process.TopicConstant.ENRICH_DATA_TOPIC;

@RegisterForReflection
public class EnrichData extends AppCommand<EnrichDataPayload, Void> {

    public EnrichData(EnrichDataPayload payload) {
        super(payload);
    }

    @Override
    public Class<Void> responseType() {
        return null;
    }

    @Override
    public String defaultTopic() {
        return ENRICH_DATA_TOPIC;
    }

    @Override
    public String toString() {
        return "EnrichData{" +
                "timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
}
