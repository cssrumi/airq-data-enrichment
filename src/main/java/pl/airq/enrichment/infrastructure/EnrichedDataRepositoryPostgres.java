package pl.airq.enrichment.infrastructure;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.infrastructure.persistance.PersistentRepositoryPostgres;
import pl.airq.enrichment.process.EnrichedDataPublisher;

@Singleton
public class EnrichedDataRepositoryPostgres extends PersistentRepositoryPostgres<EnrichedData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichedDataRepositoryPostgres.class);
    static final String UPDATE_QUERY = "UPDATE ENRICHED_DATA SET" +
            " pm10 = $1," +
            " pm25 = $2," +
            " temperature = $3," +
            " wind = $4," +
            " winddirection = $5," +
            " humidity = $6," +
            " pressure = $7," +
            " lon = $8," +
            " lat = $9," +
            " provider = $10" +
            " WHERE station = $11 AND \"timestamp\" = $12";

    static final String IS_ALREADY_EXIST_QUERY = "SELECT true FROM ENRICHED_DATA WHERE station = $1 AND timestamp = $2";

    static final String INSERT_QUERY = "INSERT INTO ENRICHED_DATA" +
            " (\"timestamp\", pm10, pm25, temperature, wind, winddirection, humidity, pressure, lon, lat, provider, station)" +
            " VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)";

    private final EnrichedDataPublisher publisher;

    @Inject
    public EnrichedDataRepositoryPostgres(PgPool client, EnrichedDataPublisher publisher) {
        super(client);
        this.publisher = publisher;
    }

    @Override
    protected String insertQuery() {
        return INSERT_QUERY;
    }

    @Override
    protected String updateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected String isAlreadyExistQuery() {
        return IS_ALREADY_EXIST_QUERY;
    }

    @Override
    protected Tuple insertTuple(EnrichedData enrichedData) {
        return Tuple.of(enrichedData.timestamp)
                    .addFloat(enrichedData.pm10)
                    .addFloat(enrichedData.pm25)
                    .addFloat(enrichedData.temp)
                    .addFloat(enrichedData.wind)
                    .addFloat(enrichedData.windDirection)
                    .addFloat(enrichedData.humidity)
                    .addFloat(enrichedData.pressure)
                    .addFloat(enrichedData.lon)
                    .addFloat(enrichedData.lat)
                    .addString(enrichedData.provider.name())
                    .addString(enrichedData.station.value());
    }

    @Override
    protected Tuple updateTuple(EnrichedData data) {
        return Tuple.of(data.pm10)
                    .addFloat(data.pm25)
                    .addFloat(data.temp)
                    .addFloat(data.wind)
                    .addFloat(data.windDirection)
                    .addFloat(data.humidity)
                    .addFloat(data.pressure)
                    .addFloat(data.lon)
                    .addFloat(data.lat)
                    .addString(data.provider.name())
                    .addString(data.station.value())
                    .addOffsetDateTime(data.timestamp);
    }

    @Override
    protected Tuple isAlreadyExistTuple(EnrichedData data) {
        return Tuple.of(data.station.value())
                    .addOffsetDateTime(data.timestamp);
    }

    @Override
    protected Uni<Void> postProcessAction(Result result, EnrichedData data) {
        return Uni.createFrom().voidItem()
                  .invoke(() -> logResult(result, data));
    }

    private void logResult(Result result, EnrichedData data) {
        if (result.isSuccess()) {
            LOGGER.info("Data has been {}.", result);
            return;
        }

        LOGGER.warn("Insertion result: {} for {}", result, data);
    }
}
