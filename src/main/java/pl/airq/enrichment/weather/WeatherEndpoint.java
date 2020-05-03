package pl.airq.enrichment.weather;

import io.smallrye.mutiny.Uni;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/v1/weather")
public class WeatherEndpoint {

    private final WeatherService weatherService;

    @Inject
    public WeatherEndpoint(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GET
    @Path("/coords/{lon}-{lat}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getWeatherInfoByCoords(@PathParam String lon, @PathParam String lat) {
        return weatherService.getCurrentWeatherInfoByCoordinates(lon, lat)
                             .map(result -> Response.ok(result).build());
    }

    @GET
    @Path("/city/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getWeatherInfoByCityInPoland(@PathParam String city) {
        return weatherService.getCurrentWeatherInfoByCityFromPoland(city)
                             .map(result -> Response.ok(result).build());
    }

    @GET
    @Path("/cache/invalidateAll")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> invalidateAllCaches() {
        return weatherService.invalidateAllCaches()
                             .map(result -> Response.ok().build());
    }
}
