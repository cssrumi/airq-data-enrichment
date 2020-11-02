package pl.airq.enrichment.infrastructure;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.infrastructure.persistance.RemovablePostgres;
import pl.airq.common.store.key.TSKey;

@ApplicationScoped
public class EnrichedDataRemovablePostgres extends RemovablePostgres<EnrichedData, TSKey> {

    @Inject
    EnrichedDataRemovablePostgres(PgPool client) {
        super(client);
    }

    @Override
    protected String removeQuery() {
        return null;
    }

    @Override
    protected Tuple removeTuple(EnrichedData data) {
        return null;
    }

    @Override
    protected Tuple removeTuple(TSKey key) {
        return null;
    }

    @Override
    protected Uni<Void> postProcessAction(Boolean isSuccess, EnrichedData data) {
        return Uni.createFrom().voidItem();
    }

    @Override
    protected Uni<Void> postProcessAction(Boolean isSuccess, TSKey key) {
        return Uni.createFrom().voidItem();
    }
}
