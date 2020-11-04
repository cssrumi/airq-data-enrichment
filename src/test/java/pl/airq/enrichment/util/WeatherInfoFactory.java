package pl.airq.enrichment.util;

import org.apache.commons.lang3.RandomUtils;
import pl.airq.enrichment.weather.WeatherInfo;

public class WeatherInfoFactory {

    private WeatherInfoFactory() {
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
