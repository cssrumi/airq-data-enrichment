package pl.airq.enrichment.domain.gios.installation;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InstallationRepositoryPostgres implements InstallationRepository {

    private static final String GET_ALL_QUERY = "SELECT * FROM gios.installations";
    private static final String GET_ALL_FROM_LAST_HOUR_QUERY = "SELECT * FROM gios.installations WHERE gios.installations.timestamp BETWEEN (NOW() - INTERVAL '1 hours' ) AND NOW()";
    private final PgPool client;

    @Inject
    public InstallationRepositoryPostgres(PgPool client) {
        this.client = client;
    }

    @Override
    public Uni<Set<Installation>> getAll() {
        return client.query(GET_ALL_QUERY).map(this::parse);
    }

    @Override
    public Uni<Set<Installation>> getAllWithPMSinceLastHour() {
        return client.query(GET_ALL_FROM_LAST_HOUR_QUERY)
                     .map(this::parse)
                     .map(installations -> installations.stream()
                                                        .filter(installation -> Optional.ofNullable(installation.code)
                                                                                        .map(code -> code.toUpperCase().contains("PM"))
                                                                                        .orElse(false))
                                                        .collect(Collectors.toSet()));
    }

    private Set<Installation> parse(RowSet<Row> pgRowSet) {
        Set<Installation> installations = new HashSet<>(pgRowSet.size());
        for (Row row : pgRowSet) {
            installations.add(Installation.from(row));
        }

        return installations;
    }
}
