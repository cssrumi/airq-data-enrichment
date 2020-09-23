package pl.airq.enrichment.process.emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EnrichDataEmitterPublicProxy {

    private final EnrichDataEmitter enrichDataEmitter;

    @Inject
    EnrichDataEmitterPublicProxy(EnrichDataEmitter enrichDataEmitter) {
        this.enrichDataEmitter = enrichDataEmitter;
    }

    public void emitEnrichDataEvent() {
        enrichDataEmitter.emitEnrichDataEvent();
    }
}
