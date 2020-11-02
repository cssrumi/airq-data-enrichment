package pl.airq.enrichment.domain.data;

import io.quarkus.test.Mock;
import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.enrichment.infrastructure.EnrichedDataRepositoryPostgres;

@Mock
@ApplicationScoped
public class MockEnrichedDataRepositoryPostgres extends EnrichedDataRepositoryPostgres {

    private Boolean result = Boolean.TRUE;

    public MockEnrichedDataRepositoryPostgres() {
        super(null, bus);
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
        this.result = result;
    }
}
