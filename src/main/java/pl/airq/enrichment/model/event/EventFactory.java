package pl.airq.enrichment.model.event;

import pl.airq.enrichment.model.command.Command;

public interface EventFactory<T> {

    <C extends Command> Event from(C command);
}
