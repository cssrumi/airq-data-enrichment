package pl.airq.enrichment.weather;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Objects;

@RegisterForReflection
public class WeatherInfo {

    public final Float temperature;
    public final Float wind;
    public final Float windDirection;
    public final Float humidity;
    public final Float pressure;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public WeatherInfo(@JsonProperty("temperature") Float temperature,
                       @JsonProperty("wind") Float wind,
                       @JsonProperty("windDirection") Float windDirection,
                       @JsonProperty("humidity") Float humidity,
                       @JsonProperty("pressure") Float pressure) {
        this.temperature = temperature;
        this.wind = wind;
        this.windDirection = windDirection;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeatherInfo that = (WeatherInfo) o;
        return Objects.equals(temperature, that.temperature) &&
                Objects.equals(wind, that.wind) &&
                Objects.equals(windDirection, that.windDirection) &&
                Objects.equals(humidity, that.humidity) &&
                Objects.equals(pressure, that.pressure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, wind, windDirection, humidity, pressure);
    }

    @Override
    public String toString() {
        return "WeatherInfo{" +
                ", temperature=" + temperature +
                ", wind=" + wind +
                ", windDirection=" + windDirection +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }
}
