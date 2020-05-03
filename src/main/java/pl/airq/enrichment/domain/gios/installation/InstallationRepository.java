package pl.airq.enrichment.domain.gios.installation;

import io.smallrye.mutiny.Uni;
import java.util.Set;

public interface InstallationRepository {

    Uni<Set<Installation>> getAll();

    Uni<Set<Installation>> getAllWithPMSinceLastHour();

}
