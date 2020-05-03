package pl.airq.enrichment.weather;

import io.smallrye.mutiny.Uni;

public interface WeatherService {

    Uni<CurrentWeatherInfo> getCurrentWeatherInfoByCityFromPoland(String city);

    Uni<CurrentWeatherInfo> getCurrentWeatherInfoByCityAndCountry(String city, String country);

    Uni<CurrentWeatherInfo> getCurrentWeatherInfoByCoordinates(String longitude, String latitude);

    Uni<Void> invalidateAllCaches();
}
