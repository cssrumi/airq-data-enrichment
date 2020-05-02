package pl.airq.weather.openweather;

import java.util.Optional;
import pl.airq.weather.CurrentWeatherInfo;
import pl.airq.weather.WeatherConverter;

final class OpenWeatherConverter implements WeatherConverter<OpenWeatherCurrentInfo> {

    @Override
    public CurrentWeatherInfo toCurrentWeatherInfo(OpenWeatherCurrentInfo dto) {
        final Optional<OpenWeatherCurrentInfo.Main> optionalMain = Optional.ofNullable(dto.main);
        final Optional<OpenWeatherCurrentInfo.Wind> optionalWind = Optional.ofNullable(dto.wind);
        final Float temperature = optionalMain.map(v -> v.temp)
                                              .orElse(null);

        final Float humidity = optionalMain.map(v -> v.humidity)
                                           .orElse(null);

        final Float pressure = optionalMain.map(v -> v.pressure)
                                           .orElse(null);

        final Float wind = optionalWind.map(v -> v.speed)
                                       .orElse(null);

        final Float windDirection = optionalWind.map(v -> v.deg)
                                                .orElse(null);

        return new CurrentWeatherInfo(temperature, wind, windDirection, humidity, pressure);
    }

}
