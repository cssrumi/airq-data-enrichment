package pl.airq.enrichment.weather.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import pl.airq.enrichment.weather.WeatherInfo;

@RegisterForReflection
public class WeatherInfoResponse {

    public final Long timestamp;
    public final WeatherInfo weatherInfo;

    @JsonCreator
    public WeatherInfoResponse(@JsonProperty("timestamp") Long timestamp,
                               @JsonProperty("weatherInfo") WeatherInfo weatherInfo) {
        this.timestamp = timestamp;
        this.weatherInfo = weatherInfo;
    }
}
