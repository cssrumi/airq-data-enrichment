package pl.airq.model.command;

import java.time.OffsetDateTime;

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
