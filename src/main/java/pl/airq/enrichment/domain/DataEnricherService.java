package pl.airq.enrichment.domain;

import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import pl.airq.common.domain.DataProvider;
import pl.airq.common.domain.enriched.EnrichedData;
import pl.airq.common.domain.gios.GiosMeasurement;
import pl.airq.common.exception.ResourceNotFoundException;
import pl.airq.enrichment.weather.WeatherClient;
import pl.airq.enrichment.weather.WeatherInfo;

@ApplicationScoped
public class DataEnricherService {

    private final WeatherClient weatherClient;

    @Inject
    public DataEnricherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public Uni<EnrichedData> enrichGiosMeasurement(GiosMeasurement measurement) {
        return weatherClient
                .getWeatherInfo(measurement.station.id, measurement.timestamp)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException(WeatherInfo.class))
                .onItem().transform(weatherInfo -> createEnrichedData(measurement, weatherInfo));
    }

    private EnrichedData createEnrichedData(GiosMeasurement measurement, WeatherInfo weatherInfo) {
        return new EnrichedData(
                measurement.timestamp,
                measurement.pm10,
                measurement.pm25,
                weatherInfo.temperature,
                weatherInfo.wind,
                weatherInfo.windDirection,
                weatherInfo.humidity,
                weatherInfo.pressure,
                measurement.station.location.getLon(),
                measurement.station.location.getLat(),
                DataProvider.GIOS,
                measurement.station.id
        );
    }
}
