package pl.airq.enrichment.process.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.airq.enrichment.model.TopicConstant;
import pl.airq.enrichment.model.event.DataEnriched;

import static io.smallrye.faulttolerance.core.util.SneakyThrow.sneakyThrow;

@ApplicationScoped
class DataEnrichedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataEnrichedHandler.class);
    private final Emitter<String> externalTopic;
    private final ObjectMapper mapper;

    @Inject
    DataEnrichedHandler(@Channel(TopicConstant.DATA_ENRICHED_EXTERNAL_TOPIC) Emitter<String> externalTopic, ObjectMapper mapper) {
        this.externalTopic = externalTopic;
        this.mapper = mapper;
    }

    @ConsumeEvent(TopicConstant.DATA_ENRICHED_TOPIC)
    Uni<Void> dataEnrichedConsumer(DataEnriched event) {
        return Uni.createFrom()
                  .item(() -> {
                      try {
                          final String rawEvent = mapper.writeValueAsString(event);
                          LOGGER.info(String.format("RawEvent: %s", rawEvent));
                          return rawEvent;
                      } catch (JsonProcessingException e) {
                          LOGGER.error(String.format("Error occurred mapping DataEnriched event: %s", event.toString()), e);
                          sneakyThrow(e);
                          return null;
                      }
                  })
                  .onItem()
                  .produceCompletionStage(externalTopic::send)
                  .onItem()
                  .invoke(ignore -> LOGGER.info(String.format("%s has been passed to External Bus.", event.getClass().getSimpleName())));
    }
}
