package pl.airq.enrichment.domain.data;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EnrichedDataQueryPostgres implements EnrichedDataQueryRepository {

    static final String FIND_ALL_QUERY = "SELECT * FROM ENRICHED_DATA";
    static final String FIND_ALL_BY_STATION_QUERY = "SELECT * FROM ENRICHED_DATA WHERE ENRICHED_DATA.STATION = $1";
    static final String FIND_ALL_COORDS_QUERY = "SELECT * FROM ENRICHED_DATA WHERE ENRICHED_DATA.LON = $1 AND ENRICHED_DATA.LAT = $2";
    static final String FIND_BY_STATION_AND_TIMESTAMP = "SELECT * FROM ENRICHED_DATA WHERE ENRICHED_DATA.STATION = $1 AND ENRICHED_DATA.TIMESTAMP = $2";
    private final PgPool client;

    @Inject
    public EnrichedDataQueryPostgres(PgPool client) {
        this.client = client;
    }

    @Override
    public Uni<Set<EnrichedData>> findAll() {
        return client.query(FIND_ALL_QUERY)
                     .map(this::parse);
    }

    @Override
    public Uni<Set<EnrichedData>> findAllByStation(String name) {
        return client.preparedQuery(FIND_ALL_BY_STATION_QUERY, Tuple.of(name))
                     .map(this::parse);
    }

    @Override
    public Uni<Set<EnrichedData>> findAllByCoords(Float lon, Float lat) {
        return client.preparedQuery(FIND_ALL_COORDS_QUERY, Tuple.of(lon).addFloat(lat))
                     .map(this::parse);
    }

    @Override
    public Uni<EnrichedData> findByStationAndTimestamp(String name, OffsetDateTime timestamp) {
        return client.preparedQuery(FIND_BY_STATION_AND_TIMESTAMP, Tuple.of(name).addOffsetDateTime(timestamp))
                     .map(rows -> EnrichedData.from(rows.iterator().next()));
    }

    private Set<EnrichedData> parse(RowSet<Row> pgRowSet) {
        Set<EnrichedData> enrichedData = new HashSet<>(pgRowSet.size());
        for (Row row : pgRowSet) {
            enrichedData.add(EnrichedData.from(row));
        }

        return enrichedData;
    }
}
