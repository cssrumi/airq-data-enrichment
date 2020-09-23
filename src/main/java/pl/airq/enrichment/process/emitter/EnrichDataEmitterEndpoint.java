package pl.airq.enrichment.process.emitter;

import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import pl.airq.common.domain.DataProvider;
import pl.airq.common.process.AppEventBus;
import pl.airq.enrichment.model.command.EnrichData;
import pl.airq.enrichment.model.command.EnrichDataPayload;

@Path("/api/emit")
public class EnrichDataEmitterEndpoint {

    private final AppEventBus eventBus;

    @Inject
    public EnrichDataEmitterEndpoint(AppEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @GET
    @Path("/enrichData")
    public Uni<Response> emitEnrichDataCommand() {
        return eventBus.request(new EnrichData(new EnrichDataPayload(List.of(DataProvider.values()))))
                       .onItem()
                       .transform(ignore -> Response.ok().build());
    }
}
