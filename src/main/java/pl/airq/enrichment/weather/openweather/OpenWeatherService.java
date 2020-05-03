package pl.airq.enrichment.weather.openweather;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import pl.airq.enrichment.weather.CurrentWeatherInfo;
import pl.airq.enrichment.weather.WeatherService;

@ApplicationScoped
class OpenWeatherService implements WeatherService {

    static final String CURRENT_WEATHER_BY_CITY_AND_COUNTRY = "CurrentWeatherByCityAndCountry";
    static final String CURRENT_WEATHER_BY_CITY_IN_POLAND = "CurrentWeatherByCityInPoland";
    static final String CURRENT_WEATHER_BY_COORDINATES = "CurrentWeatherByCoordinates";
    private final String apiKey;
    private final OpenWeatherClient client;
    private final OpenWeatherConverter converter;

    @Inject
    public OpenWeatherService(@ConfigProperty(name = "data-enrichment.open-weather.api-key") String apiKey,
                              @RestClient OpenWeatherClient client) {
        this.apiKey = apiKey;
        this.client = client;
        this.converter = new OpenWeatherConverter();
    }

    @Override
    @CacheResult(cacheName = CURRENT_WEATHER_BY_CITY_AND_COUNTRY)
    public Uni<CurrentWeatherInfo> getCurrentWeatherInfoByCityAndCountry(String city, String country) {
        return client.getByCityAndCountry(city + "," + country, apiKey)
                     .map(converter::toCurrentWeatherInfo);
    }

    @Override
    @CacheResult(cacheName = CURRENT_WEATHER_BY_CITY_IN_POLAND)
    public Uni<CurrentWeatherInfo> getCurrentWeatherInfoByCityFromPoland(String city) {
        return client.getByCityAndCountry(city + ",PL", apiKey)
                     .map(converter::toCurrentWeatherInfo);
    }

    @Override
    @CacheResult(cacheName = CURRENT_WEATHER_BY_COORDINATES)
    public Uni<CurrentWeatherInfo> getCurrentWeatherInfoByCoordinates(String longitude, String latitude) {
        return client.getByCoordinates(longitude, latitude, apiKey)
                     .map(converter::toCurrentWeatherInfo);
    }

    @Override
    public Uni<Void> invalidateAllCaches() {
        return Uni.createFrom().item(() -> {
            invalidateCurrentWeatherByCityAndCountry();
            invalidateCurrentWeatherByCityInPoland();
            invalidateCurrentWeatherByCoordinates();
            return null;
        });
    }

    @CacheInvalidateAll(cacheName = CURRENT_WEATHER_BY_CITY_AND_COUNTRY)
    void invalidateCurrentWeatherByCityAndCountry() {
    }

    @CacheInvalidateAll(cacheName = CURRENT_WEATHER_BY_CITY_IN_POLAND)
    void invalidateCurrentWeatherByCityInPoland() {
    }

    @CacheInvalidateAll(cacheName = CURRENT_WEATHER_BY_COORDINATES)
    void invalidateCurrentWeatherByCoordinates() {
    }
}
