package pl.airq.enrichment.infrastructure;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.infrastructure.persistance.PersistentRepositoryPostgres;
import pl.airq.common.process.AppEventBus;
import pl.airq.enrichment.process.DataEnrichedPublisher;

@Singleton
public class EnrichedDataRepositoryPostgres extends PersistentRepositoryPostgres<EnrichedData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichedDataRepositoryPostgres.class);
    static final String ON_CONFLICT_UPDATE_PART = " ON CONFLICT (\"timestamp\", station) DO UPDATE SET" +
            " pm10 = EXCLUDED.pm10, pm25 = EXCLUDED.pm25, temperature = EXCLUDED.temperature, wind = EXCLUDED.wind," +
            " winddirection = EXCLUDED.winddirection, humidity = EXCLUDED.humidity, pressure = EXCLUDED.pressure," +
            " lon = EXCLUDED.lon, lat = EXCLUDED.lat, provider = EXCLUDED.provider";
    static final String INSERT_QUERY = "INSERT INTO ENRICHED_DATA" +
            " (\"timestamp\", pm10, pm25, temperature, wind, winddirection, humidity, pressure, lon, lat, provider, station)" +
            " VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)";
    static final String UPSERT_QUERY = INSERT_QUERY + ON_CONFLICT_UPDATE_PART;

    private final DataEnrichedPublisher publisher;

    @Inject
    public EnrichedDataRepositoryPostgres(PgPool client, DataEnrichedPublisher publisher) {
        super(client);
        this.publisher = publisher;
    }

    @Override
    protected String insertQuery() {
        return INSERT_QUERY;
    }

    @Override
    protected String upsertQuery() {
        return UPSERT_QUERY;
    }

    @Override
    protected Tuple prepareTuple(EnrichedData enrichedData) {
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
    protected Uni<Void> postProcessAction(Action action, Boolean result, EnrichedData data) {
        return Uni.createFrom().voidItem()
                  .invoke(() -> logResult(action, result, data));
    }

    private void logResult(Action action, Boolean result, EnrichedData data) {
        if (Boolean.TRUE.equals(result)) {
            LOGGER.info("{} completed.", action);
            return;
        }

        LOGGER.warn("Unable to perform {} for {}", action, data);
    }
}
