package pl.airq.model.command;

import io.smallrye.mutiny.Uni;
import pl.airq.model.Try;

public interface Respond<C extends Command> {

    Uni<Try> respond(C command);

}
