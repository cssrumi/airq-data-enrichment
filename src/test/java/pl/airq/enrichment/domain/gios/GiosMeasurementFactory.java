package pl.airq.enrichment.domain.gios;

import pl.airq.common.domain.gios.installation.Installation;

public class GiosMeasurementFactory {

    public static GiosMeasurement giosMeasurement(Installation installation) {
        return GiosMeasurement.from(installation);
    }

}
