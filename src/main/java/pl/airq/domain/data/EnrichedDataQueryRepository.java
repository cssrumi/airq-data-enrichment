package pl.airq.domain.data;

import io.smallrye.mutiny.Uni;
import java.time.OffsetDateTime;
import java.util.Set;

public interface EnrichedDataQueryRepository {

    Uni<Set<EnrichedData>> findAll();

    Uni<Set<EnrichedData>> findAllByStation(String name);

    Uni<Set<EnrichedData>> findAllByCoords(Float lon, Float lat);

    Uni<EnrichedData> findByStationAndTimestamp(String name, OffsetDateTime timestamp);

}
