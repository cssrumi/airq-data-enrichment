package pl.airq.enrichment.model.event;

import java.time.OffsetDateTime;
import pl.airq.enrichment.model.Payload;

public abstract class Event<P extends Payload> {

    public final OffsetDateTime dateTime;
    public final P payload;
    public final String eventType;

    public Event(OffsetDateTime dateTime, P payload, String eventType) {
        this.dateTime = dateTime;
        this.payload = payload;
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "dateTime=" + dateTime +
                ", payload=" + payload +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
