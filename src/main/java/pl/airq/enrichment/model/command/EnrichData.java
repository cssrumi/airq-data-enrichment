package pl.airq.enrichment.model.command;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.OffsetDateTime;

@RegisterForReflection
public class EnrichData extends Command<EnrichDataPayload> {

    public EnrichData(OffsetDateTime dateTime, EnrichDataPayload payload) {
        super(dateTime, payload, EnrichData.class.getSimpleName());
    }

    @Override
    public String toString() {
        return "EnrichData{" +
                "dateTime=" + dateTime +
                ", payload=" + payload +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
