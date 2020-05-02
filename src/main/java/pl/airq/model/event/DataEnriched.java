package pl.airq.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.OffsetDateTime;

@RegisterForReflection
public class DataEnriched extends Event<DataEnrichedPayload> {

    public DataEnriched(OffsetDateTime dateTime, DataEnrichedPayload payload) {
        super(dateTime, payload, DataEnriched.class.getSimpleName());
    }

    @Override
    public String toString() {
        return "DataEnriched{" +
                "dateTime=" + dateTime +
                ", payload=" + payload +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
