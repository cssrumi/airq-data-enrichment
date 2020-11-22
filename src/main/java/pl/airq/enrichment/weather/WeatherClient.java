package pl.airq.enrichment.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import java.time.OffsetDateTime;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.common.vo.StationId;
import pl.airq.enrichment.config.DataEnrichmentProperties;
import pl.airq.enrichment.weather.dto.WeatherInfoRequest;
import pl.airq.enrichment.weather.dto.WeatherInfoResponse;

@ApplicationScoped
public class WeatherClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherClient.class);

    private final ObjectMapper mapper;
    private final WebClient client;

    @Inject
    WeatherClient(DataEnrichmentProperties properties, Vertx vertx, ObjectMapper mapper) {
        this.mapper = mapper;
        this.client = WebClient.create(vertx, new WebClientOptions().setDefaultHost(properties.getWeather().getHost())
                                                                    .setDefaultPort(properties.getWeather().getPort())
                                                                    .setSsl(properties.getWeather().getSsl())
                                                                    .setTrustAll(true));
    }

    public Uni<WeatherInfo> getWeatherInfo(StationId stationId, OffsetDateTime timestamp) {
        return client.post("/v1/weather/info")
                     .sendJson(WeatherInfoRequest.from(timestamp, stationId))
                     .map(this::deserialize)
                     .map(weatherInfoResponse -> weatherInfoResponse.weatherInfo);
    }

    private WeatherInfoResponse deserialize(HttpResponse<?> response) {
        final Response.Status status = Response.Status.fromStatusCode(response.statusCode());
        if (!status.getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOGGER.warn("Unhandled status: {}", status);
            throw new RuntimeException(String.format("Unhandled status: %s", status));//todo: create valid exception
        }
        try {
            return mapper.readValue(response.bodyAsString(), WeatherInfoResponse.class);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Unable to deserialize {}. Raw msg: {}", WeatherInfoResponse.class.getSimpleName(), response.toString());
            throw new RuntimeException(e);//todo: create valid exception
        }
    }
}
