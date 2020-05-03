package pl.airq.enrichment.model.command;

import java.time.OffsetDateTime;
import pl.airq.enrichment.model.Payload;
import pl.airq.enrichment.model.event.Event;

public abstract class Command<P extends Payload> extends Event<P> {

    public Command(OffsetDateTime dateTime, P payload, String eventType) {
        super(dateTime, payload, eventType);
    }

    @Override
    public String toString() {
        return "Command{" +
                "dateTime=" + dateTime +
                ", payload=" + payload +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
