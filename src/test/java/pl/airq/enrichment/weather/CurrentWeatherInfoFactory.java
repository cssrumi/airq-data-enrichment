package pl.airq.enrichment.weather;

import org.apache.commons.lang3.RandomUtils;

public class CurrentWeatherInfoFactory {

    private CurrentWeatherInfoFactory() {
    }

    public static CurrentWeatherInfo random() {
        return new CurrentWeatherInfo(
                RandomUtils.nextFloat(),
                RandomUtils.nextFloat(),
                RandomUtils.nextFloat(),
                RandomUtils.nextFloat(),
                RandomUtils.nextFloat()
        );
    }

}
