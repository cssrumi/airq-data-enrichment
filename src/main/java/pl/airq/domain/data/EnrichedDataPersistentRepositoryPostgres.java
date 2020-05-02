package pl.airq.domain.data;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.domain.PersistentRepository;

@ApplicationScoped
public class EnrichedDataPersistentRepositoryPostgres implements PersistentRepository<EnrichedData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichedDataPersistentRepositoryPostgres.class);
    static final String INSERT_QUERY = "INSERT INTO ENRICHED_DATA (\"timestamp\", pm10, pm25, temperature, wind, winddirection, humidity, pressure, lon, lat, provider, station) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)";
    private final PgPool client;
    private final EnrichedDataQueryRepository dataQueryRepository;

    @Inject
    public EnrichedDataPersistentRepositoryPostgres(PgPool client, EnrichedDataQueryRepository dataQueryRepository) {
        this.client = client;
        this.dataQueryRepository = dataQueryRepository;
    }

    @Override
    public Uni<Boolean> save(EnrichedData data) {
        return client.preparedQuery(INSERT_QUERY, prepareEnrichedDataTuple(data))
                     .onItem()
                     .apply(result -> {
                         if (result.rowCount() != 0) {
                             LOGGER.debug("EnrichedData saved successfully.");
                             return true;
                         }

                         LOGGER.warn("Unable to save EnrichedData: " + data);
                         return false;
                     });
    }

    @Override
    public Uni<Boolean> upsert(EnrichedData data) {
        // TODO: UPDATE LOGIC
        return save(data);
    }

    private Tuple prepareEnrichedDataTuple(EnrichedData enrichedData) {
        return Tuple.of(enrichedData.getTimestamp())
                    .addFloat(enrichedData.getPm10())
                    .addFloat(enrichedData.getPm25())
                    .addFloat(enrichedData.getTemp())
                    .addFloat(enrichedData.getWind())
                    .addFloat(enrichedData.getWindDirection())
                    .addFloat(enrichedData.getHumidity())
                    .addFloat(enrichedData.getPressure())
                    .addFloat(enrichedData.getLon())
                    .addFloat(enrichedData.getLat())
                    .addString(enrichedData.getProvider().name())
                    .addString(enrichedData.getStation());
    }
}
