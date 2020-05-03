package pl.airq.enrichment.weather;

public interface WeatherConverter<T> {

    CurrentWeatherInfo toCurrentWeatherInfo(T dto);
}
