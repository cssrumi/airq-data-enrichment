package pl.airq.weather;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CurrentWeatherInfo {

    public final Float temperature;
    public final Float wind;
    public final Float windDirection;
    public final Float humidity;
    public final Float pressure;

    public CurrentWeatherInfo(Float temperature, Float wind, Float windDirection, Float humidity, Float pressure) {
        this.temperature = temperature;
        this.wind = wind;
        this.windDirection = windDirection;
        this.humidity = humidity;
        this.pressure = pressure;
    }
}
