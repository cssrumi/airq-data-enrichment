package pl.airq.enrichment.weather.openweather;

import io.smallrye.mutiny.Uni;
import java.time.temporal.ChronoUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@Path("/2.5")
@RegisterRestClient(configKey = "data-enrichment.open-weather.api")
public interface OpenWeatherClient {

    @GET
    @Retry(maxRetries = 3, delay = 3, delayUnit = ChronoUnit.SECONDS)
    @Path("/weather")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<OpenWeatherCurrentInfo> getByCityAndCountry(@QueryParam("q") String cityCommaCountry,
                                                    @QueryParam("appid") String apiKey);

    @GET
    @Retry(maxRetries = 3, delay = 3, delayUnit = ChronoUnit.SECONDS)
    @Path("/weather")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<OpenWeatherCurrentInfo> getByCoordinates(@QueryParam("lon") String longitude,
                                                 @QueryParam("lat") String latitude,
                                                 @QueryParam("appid") String apiKey);
}
