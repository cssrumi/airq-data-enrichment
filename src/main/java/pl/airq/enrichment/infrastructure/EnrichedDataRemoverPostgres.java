package pl.airq.enrichment.infrastructure;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.infrastructure.persistance.RemovablePostgres;
import pl.airq.common.store.key.TSKey;

@Singleton
public class EnrichedDataRemoverPostgres extends RemovablePostgres<EnrichedData, TSKey> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichedDataRepositoryPostgres.class);
    private static final String REMOVE_QUERY = "DELETE FROM ENRICHED_DATA WHERE station = $1 AND timestamp = $2";

    @Inject
    EnrichedDataRemoverPostgres(PgPool client) {
        super(client);
    }

    @Override
    protected String removeQuery() {
        return REMOVE_QUERY;
    }

    @Override
    protected Tuple removeTuple(EnrichedData data) {
        return Tuple.of(data.station.value())
                    .addOffsetDateTime(data.timestamp);
    }

    @Override
    protected Tuple removeTuple(TSKey key) {
        return Tuple.of(key.station())
                    .addOffsetDateTime(key.timestamp());
    }

    @Override
    protected Uni<Void> postProcessAction(Boolean isSuccess, EnrichedData data) {
        if (isSuccess) {
            return Uni.createFrom().voidItem()
                      .invoke(() -> LOGGER.info("EnrichedData: {} has been removed.", data));
        }

        return Uni.createFrom().voidItem()
                  .invoke(() -> LOGGER.warn("Unable to remove EnrichedData: {}", data));
    }

    @Override
    protected Uni<Void> postProcessAction(Boolean isSuccess, TSKey key) {
        if (isSuccess) {
            return Uni.createFrom().voidItem()
                      .invoke(() -> LOGGER.info("EnrichedData for key: {} has been removed.", key.value()));
        }

        return Uni.createFrom().voidItem()
                  .invoke(() -> LOGGER.warn("Unable to remove EnrichedData for key: {}", key.value()));
    }
}
