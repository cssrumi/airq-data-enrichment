package pl.airq.enrichment.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.time.OffsetDateTime;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import pl.airq.common.vo.StationId;
import pl.airq.enrichment.weather.dto.WeatherInfoResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTestResource(WeatherApiMockResource.class)
@QuarkusTest
class WeatherClientTest {

    private static final String URI = "/v1/weather/info";
    private final WireMockServer server = WeatherApiMockResource.server();

    @Inject
    ObjectMapper mapper;

    @Inject
    WeatherClient client;

    @Test
    void getWeatherInfo_with200_expectValidWeatherInfo() throws JsonProcessingException {
        WeatherInfoResponse expectedResponse = weatherInfoResponse();
        stubFor(post(urlPathEqualTo(URI)).willReturn(aResponse().withStatus(200)
                                                                .withBody(mapper.writeValueAsString(expectedResponse))));

        WeatherInfo result = client.getWeatherInfo(StationId.EMPTY, OffsetDateTime.now())
                                   .await().atMost(Duration.ofSeconds(3));

        assertThat(result).isNotNull();
        assertThat(result.temperature).isEqualTo(expectedResponse.weatherInfo.temperature);
        assertThat(result.humidity).isEqualTo(expectedResponse.weatherInfo.humidity);
        assertThat(result.pressure).isEqualTo(expectedResponse.weatherInfo.pressure);
        assertThat(result.wind).isEqualTo(expectedResponse.weatherInfo.wind);
        assertThat(result.windDirection).isEqualTo(expectedResponse.weatherInfo.windDirection);
    }

    private WeatherInfoResponse weatherInfoResponse() {
        WeatherInfo info = new WeatherInfo(1f, 2f, 3f, 4f, 5f);
        return new WeatherInfoResponse(OffsetDateTime.now().toEpochSecond(), info);
    }
}
