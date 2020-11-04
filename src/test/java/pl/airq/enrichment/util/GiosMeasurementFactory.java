package pl.airq.enrichment.util;


import pl.airq.common.domain.gios.GiosMeasurement;
import pl.airq.common.domain.gios.Installation;

public class GiosMeasurementFactory {

    public static GiosMeasurement giosMeasurement(Installation installation) {
        return GiosMeasurement.from(installation);
    }

}
