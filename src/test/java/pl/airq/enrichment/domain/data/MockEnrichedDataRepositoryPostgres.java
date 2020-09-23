package pl.airq.enrichment.domain.data;

import io.quarkus.test.Mock;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import javax.enterprise.context.ApplicationScoped;
import org.junit.jupiter.api.Test;
import pl.airq.common.domain.enriched.EnrichedData;

import static org.junit.jupiter.api.Assertions.*;

@Mock
@ApplicationScoped
public class MockEnrichedDataRepositoryPostgres extends EnrichedDataRepositoryPostgres{

    private Boolean result = Boolean.TRUE;

    public MockEnrichedDataRepositoryPostgres() {
        super(null);
    }

    @Override
    public Uni<Boolean> save(EnrichedData data) {
        return Uni.createFrom().item(result);
    }

    @Override
    public Uni<Boolean> upsert(EnrichedData data) {
        return Uni.createFrom().item(result);
    }

    public void setSaveAndUpsertResult(Boolean result) {
    }
}
