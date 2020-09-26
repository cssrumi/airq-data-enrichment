package pl.airq.enrichment.domain.gios;

import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import pl.airq.common.domain.gios.installation.Installation;
import pl.airq.common.domain.gios.installation.InstallationQuery;

import static java.util.stream.Collectors.groupingBy;

@ApplicationScoped
public class GiosService {

    private final InstallationQuery installationQuery;

    @Inject
    public GiosService(InstallationQuery installationQuery) {
        this.installationQuery = installationQuery;
    }

    public Uni<Set<Installation>> getInstallationsSinceLastHour() {
        return installationQuery.getAllWithPMSinceLastHour();
    }

    public Uni<Set<GiosMeasurement>> getMeasurementsSinceLastHour() {
        return installationQuery.getAllWithPMSinceLastHour()
                                .map(installations ->
                                        mapInstallationsToGiosMeasurements(installations.stream()
                                                                                        .collect(groupingBy(installation -> installation.id))));
    }

    private Set<GiosMeasurement> mapInstallationsToGiosMeasurements(Map<Long, List<Installation>> installationsPerStation) {
        return installationsPerStation.values()
                                      .stream()
                                      .map(installations -> installations.stream()
                                                                         .map(GiosMeasurement::from)
                                                                         .reduce(GiosMeasurement.empty(), (merged, measurement) ->
                                                                                 merged.merge(measurement)))
                                      .collect(Collectors.toSet());
    }

}
