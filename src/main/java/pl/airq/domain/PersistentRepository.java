package pl.airq.domain;

import io.smallrye.mutiny.Uni;

public interface PersistentRepository<T> {

    Uni<Boolean> save(T measurement);

    Uni<Boolean> upsert(T measurement);

}
