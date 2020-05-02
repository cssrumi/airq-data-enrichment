package pl.airq.model.event;

import pl.airq.model.command.Command;

public interface EventFactory<T> {

    <C extends Command> Event from(C command);
}
