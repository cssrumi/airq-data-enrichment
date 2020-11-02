package pl.airq.enrichment.config;

import io.quarkus.arc.config.ConfigProperties;
import javax.validation.constraints.NotNull;
import pl.airq.common.config.properties.RestService;

@ConfigProperties(prefix = "data-enrichment")
public class DataEnrichmentProperties {

    @NotNull
    private RestService weather;

    public RestService getWeather() {
        return weather;
    }

    public void setWeather(RestService weather) {
        this.weather = weather;
    }
}
