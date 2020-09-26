package pl.airq.enrichment.domain.data;

import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.domain.PersistentRepository;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.enrichment.domain.gios.GiosMeasurement;
import pl.airq.enrichment.weather.WeatherService;

@ApplicationScoped
public class DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);
    private final PersistentRepository<EnrichedData> enrichedDataRepository;
    private final WeatherService weatherService;

    @Inject
    public DataService(PersistentRepository<EnrichedData> enrichedDataRepository, WeatherService weatherService) {
        this.enrichedDataRepository = enrichedDataRepository;
        this.weatherService = weatherService;
    }

    public Uni<EnrichedData> enrichGiosData(GiosMeasurement giosMeasurement) {
        return weatherService.getCurrentWeatherInfoByCoordinates(giosMeasurement.lon.toString(), giosMeasurement.lat.toString())
                             .onItem().transform(currentWeatherInfo -> {
                                 LOGGER.info("Enriching gios measurement: " + giosMeasurement);
                                 return giosMeasurement.enrich(currentWeatherInfo);
                             });
    }

    public Uni<Void> save(EnrichedData enrichedData) {
        return enrichedDataRepository.save(enrichedData)
                                     .onItem().transform(this::checkResult);
    }

    private Void checkResult(Boolean result) {
        if (Boolean.FALSE.equals(result)) {
            throw new RuntimeException("Unable to save EnrichedData.");
        }

        return null;
    }
}
