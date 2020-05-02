package pl.airq.domain.data;

import io.vertx.mutiny.sqlclient.Row;
import java.time.OffsetDateTime;
import pl.airq.domain.DataProvider;
import pl.airq.domain.gios.GiosMeasurement;
import pl.airq.weather.CurrentWeatherInfo;

public final class EnrichedData {

    private final OffsetDateTime timestamp;
    private final Float pm10;
    private final Float pm25;
    private final Float temp;
    private final Float wind;
    private final Float windDirection;
    private final Float humidity;
    private final Float pressure;
    private final Float lon;
    private final Float lat;
    private final DataProvider provider;
    private final String station;


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

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public Float getPm10() {
        return pm10;
    }

    public Float getPm25() {
        return pm25;
    }

    public Float getTemp() {
        return temp;
    }

    public Float getWind() {
        return wind;
    }

    public Float getWindDirection() {
        return windDirection;
    }

    public Float getHumidity() {
        return humidity;
    }

    public Float getPressure() {
        return pressure;
    }

    public Float getLon() {
        return lon;
    }

    public Float getLat() {
        return lat;
    }

    public DataProvider getProvider() {
        return provider;
    }

    public String getStation() {
        return station;
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
