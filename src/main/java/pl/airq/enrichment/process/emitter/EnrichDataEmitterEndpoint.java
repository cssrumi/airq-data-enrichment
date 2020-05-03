package pl.airq.enrichment.process.emitter;

import io.smallrye.mutiny.Uni;
import java.time.OffsetDateTime;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import pl.airq.enrichment.domain.DataProvider;
import pl.airq.enrichment.model.TopicConstant;
import pl.airq.enrichment.process.AirqEventBus;
import pl.airq.enrichment.model.command.EnrichData;
import pl.airq.enrichment.model.command.EnrichDataPayload;

@Path("/v1/emit")
public class EnrichDataEmitterEndpoint {

    private final AirqEventBus eventBus;

    @Inject
    public EnrichDataEmitterEndpoint(AirqEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @GET
    @Path("/enrichData")
    public Uni<Response> emitEnrichDataCommand() {
        return eventBus.request(TopicConstant.ENRICH_DATA_TOPIC, new EnrichData(OffsetDateTime.now(), new EnrichDataPayload(List.of(DataProvider.values()))))
                .onItem()
                .apply(ignore -> Response.ok().build());
    }
}
