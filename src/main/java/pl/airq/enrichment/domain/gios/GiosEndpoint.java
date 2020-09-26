package pl.airq.enrichment.domain.gios;

import io.smallrye.mutiny.Uni;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/gios")
@Produces(MediaType.APPLICATION_JSON)
public class GiosEndpoint {

    private final GiosService giosService;

    @Inject
    public GiosEndpoint(GiosService giosService) {
        this.giosService = giosService;
    }

    @GET
    @Path("/lastHour/installations")
    public Uni<Response> getAllInstallationsWithPMFromLastHour() {
        return giosService.getInstallationsSinceLastHour()
                          .map(installations -> Response.ok(installations).build());
    }

    @GET
    @Path("/lastHour/measurements")
    public Uni<Response> getAllMeasurementsWithPMFromLastHour() {
        return giosService.getMeasurementsSinceLastHour()
                          .map(installations -> Response.ok(installations).build());
    }
}
