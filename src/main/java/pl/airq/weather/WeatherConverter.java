package pl.airq.weather;

public interface WeatherConverter<T> {

    CurrentWeatherInfo toCurrentWeatherInfo(T dto);
}
