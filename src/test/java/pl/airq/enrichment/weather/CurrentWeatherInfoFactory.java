package pl.airq.enrichment.weather;

import org.apache.commons.lang3.RandomUtils;

public class CurrentWeatherInfoFactory {

    private CurrentWeatherInfoFactory() {
    }

    public static WeatherInfo random() {
        return new WeatherInfo(
                RandomUtils.nextFloat(),
                RandomUtils.nextFloat(),
                RandomUtils.nextFloat(),
                RandomUtils.nextFloat(),
                RandomUtils.nextFloat()
        );
    }

}
