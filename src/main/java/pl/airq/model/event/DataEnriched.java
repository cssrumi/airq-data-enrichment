package pl.airq.model.event;

import java.time.OffsetDateTime;

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
