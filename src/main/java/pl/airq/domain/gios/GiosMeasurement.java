package pl.airq.domain.gios;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.OffsetDateTime;
import pl.airq.domain.gios.installation.Installation;

@RegisterForReflection
public class GiosMeasurement {

    private static final GiosMeasurement EMPTY = new GiosMeasurement(null, null, null, null, null, null);

    public final String name;
    public final OffsetDateTime timestamp;
    public final Float pm10;
    public final Float pm25;
    public final Float lon;
    public final Float lat;

    GiosMeasurement(String name, OffsetDateTime timestamp, Float pm10, Float pm25, Float lon, Float lat) {
        this.name = name;
        this.timestamp = timestamp;
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.lon = lon;
        this.lat = lat;
    }

    Builder toBuilder() {
        return builder().name(name)
                        .timestamp(timestamp)
                        .pm10(pm10)
                        .pm25(pm25)
                        .lat(lat)
                        .lon(lon);
    }

    GiosMeasurement merge(GiosMeasurement giosMeasurement) {
        final Builder builder = giosMeasurement.toBuilder();
        if (pm10 != null) {
            builder.pm10(pm10);
        }

        if (pm25 != null) {
            builder.pm25(pm25);
        }

        return builder.build();
    }

    static Builder builder() {
        return new Builder();
    }

    static GiosMeasurement empty() {
        return EMPTY;
    }

    static class Builder {
        private String name;
        private OffsetDateTime timestamp;
        private Float pm10;
        private Float pm25;
        private Float lon;
        private Float lat;

        Builder() {
        }

        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder timestamp(OffsetDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        Builder pm10(Float pm10) {
            this.pm10 = pm10;
            return this;
        }

        Builder pm25(Float pm25) {
            this.pm25 = pm25;
            return this;
        }

        Builder lon(Float lon) {
            this.lon = lon;
            return this;
        }

        Builder lat(Float lat) {
            this.lat = lat;
            return this;
        }

        GiosMeasurement build() {
            return new GiosMeasurement(name, timestamp, pm10, pm25, lon, lat);
        }

    }

    static GiosMeasurement from(Installation installation) {
        final String code = installation.code.toUpperCase();
        if (code.contains("PM10")) {
            return new GiosMeasurement(installation.name, installation.timestamp, installation.value, null, installation.lon, installation.lat);
        }

        if (code.contains("PM25") || code.contains("PM2.5")) {
            return new GiosMeasurement(installation.name, installation.timestamp, null, installation.value, installation.lon, installation.lat);
        }

        throw new UnsupportedOperationException("Invalid installation type: " + installation.toString());
    }

    static GiosMeasurement merge(GiosMeasurement giosMeasurement, Installation installation) {
        final Builder builder = giosMeasurement.toBuilder();
        return merge(builder, installation);
    }

    static GiosMeasurement merge(Builder giosMeasurementBuilder, Installation installation) {
        final String code = installation.code.toUpperCase();
        if (code.contains("PM10")) {
            giosMeasurementBuilder.pm10(installation.value);
        }

        if (code.contains("PM25") || code.contains("PM2.5")) {
            giosMeasurementBuilder.pm25(installation.value);
        }

        return giosMeasurementBuilder.build();
    }
}
