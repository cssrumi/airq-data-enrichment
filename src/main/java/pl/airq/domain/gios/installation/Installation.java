package pl.airq.domain.gios.installation;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.mutiny.sqlclient.Row;
import java.time.OffsetDateTime;

@RegisterForReflection
public class Installation {

    public final Long id;
    public final String name;
    public final OffsetDateTime timestamp;
    public final Float value;
    public final Float lon;
    public final Float lat;
    public final String code;

    private Installation(Long id, String name, OffsetDateTime timestamp, Float value, Float lon, Float lat, String code) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.value = value;
        this.lon = lon;
        this.lat = lat;
        this.code = code;
    }

    static Installation from(Row row) {
        return new Installation(row.getLong("id"), row.getString("name"),
                row.getOffsetDateTime("timestamp"), row.getFloat("value"),
                row.getFloat("y"), row.getFloat("x"),
                row.getString("code"));
    }
}
