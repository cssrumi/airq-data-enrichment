package pl.airq.enrichment.domain.data;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.mutiny.sqlclient.Row;
import java.time.OffsetDateTime;
import pl.airq.enrichment.domain.DataProvider;
import pl.airq.enrichment.domain.gios.GiosMeasurement;
import pl.airq.enrichment.weather.CurrentWeatherInfo;

@RegisterForReflection
public final class EnrichedData {

    public final OffsetDateTime timestamp;
    public final Float pm10;
    public final Float pm25;
    public final Float temp;
    public final Float wind;
    public final Float windDirection;
    public final Float humidity;
    public final Float pressure;
    public final Float lon;
    public final Float lat;
    public final DataProvider provider;
    public final String station;


    public EnrichedData(OffsetDateTime timestamp, Float pm10, Float pm25, Float temp, Float wind, Float windDirection, Float humidity, Float pressure,
                        Float lon, Float lat, DataProvider provider, String station) {
        this.timestamp = timestamp;
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.temp = temp;
        this.wind = wind;
        this.windDirection = windDirection;
        this.humidity = humidity;
        this.pressure = pressure;
        this.lon = lon;
        this.lat = lat;
        this.provider = provider;
        this.station = station;
    }

    public static EnrichedData from(Row row) {
        return new EnrichedData(
                row.getOffsetDateTime("timestamp"), row.getFloat("pm10"), row.getFloat("pm25"),
                row.getFloat("temperature"), row.getFloat("wind"), row.getFloat("winddirection"),
                row.getFloat("humidity"), row.getFloat("pressure"), row.getFloat("lon"),
                row.getFloat("lat"), DataProvider.valueOf(row.getString("provider")), row.getString("station")
        );
    }

    @Override
    public String toString() {
        return "EnrichedData{" +
                "timestamp=" + timestamp +
                ", pm10=" + pm10 +
                ", pm25=" + pm25 +
                ", temp=" + temp +
                ", wind=" + wind +
                ", windDirection=" + windDirection +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", lon=" + lon +
                ", lat=" + lat +
                ", provider=" + provider +
                ", station='" + station + '\'' +
                '}';
    }

    static EnrichedData enrichGiosData(GiosMeasurement giosMeasurement, CurrentWeatherInfo weatherInfo) {
        return new EnrichedData(
                giosMeasurement.timestamp, giosMeasurement.pm10, giosMeasurement.pm25,
                weatherInfo.temperature, weatherInfo.wind, weatherInfo.windDirection,
                weatherInfo.humidity, weatherInfo.pressure, giosMeasurement.lon,
                giosMeasurement.lat, DataProvider.GIOS, giosMeasurement.name
        );
    }
}
