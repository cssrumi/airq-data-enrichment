package pl.airq.enrichment.weather.dto;

import com.google.common.base.Preconditions;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import pl.airq.common.vo.StationId;

@RegisterForReflection
public class WeatherInfoRequest {

    public final Long timestamp;
    public final String stationId;

    public WeatherInfoRequest(@NotNull Long timestamp, @NotNull String stationId) {
        Preconditions.checkNotNull(timestamp);
        Preconditions.checkNotNull(stationId);
        this.timestamp = timestamp;
        this.stationId = stationId;
    }

    public static WeatherInfoRequest from(@NotNull OffsetDateTime timestamp, StationId stationId) {
        Preconditions.checkNotNull(timestamp);
        Preconditions.checkNotNull(stationId);
        Long convertedTimestamp = timestamp.toEpochSecond();
        String convertedStationId = stationId.value();
        return new WeatherInfoRequest(convertedTimestamp, convertedStationId);
    }
}
