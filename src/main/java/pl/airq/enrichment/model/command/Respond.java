package pl.airq.enrichment.model.command;

import io.smallrye.mutiny.Uni;
import pl.airq.enrichment.model.Try;

public interface Respond<C extends Command> {

    Uni<Try> respond(C command);

}
