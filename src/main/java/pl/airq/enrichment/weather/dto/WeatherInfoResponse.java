package pl.airq.enrichment.weather.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import pl.airq.enrichment.weather.WeatherInfo;

@RegisterForReflection
public class WeatherInfoResponse {

    public final Long timestamp;
    public final WeatherInfo weatherInfo;

    public WeatherInfoResponse(Long timestamp, WeatherInfo weatherInfo) {
        this.timestamp = timestamp;
        this.weatherInfo = weatherInfo;
    }
}
