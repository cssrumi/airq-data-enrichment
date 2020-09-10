package pl.airq.enrichment.domain.data;

import io.smallrye.mutiny.Uni;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.airq.common.domain.enriched.EnrichedDataQuery;

@Produces(MediaType.APPLICATION_JSON)
@Path("/v1/data/enriched")
public class EnrichedDataEndpoint {

    private final EnrichedDataQuery queryRepository;

    @Inject
    public EnrichedDataEndpoint(EnrichedDataQuery queryRepository) {
        this.queryRepository = queryRepository;
    }

    @GET
    public Uni<Response> findAll() {
        return queryRepository.findAll()
                              .map(result -> Response.ok(result).build());
    }

}
