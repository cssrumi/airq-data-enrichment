package pl.airq.process.event;

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
import pl.airq.model.event.DataEnriched;

import static io.smallrye.faulttolerance.core.util.SneakyThrow.sneakyThrow;
import static pl.airq.model.TopicConstant.DATA_ENRICHED_EXTERNAL_TOPIC;
import static pl.airq.model.TopicConstant.DATA_ENRICHED_TOPIC;

@ApplicationScoped
class DataEnrichedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataEnrichedHandler.class);
    private final Emitter<String> externalTopic;
    private final ObjectMapper mapper;

    @Inject
    DataEnrichedHandler(@Channel(DATA_ENRICHED_EXTERNAL_TOPIC) Emitter<String> externalTopic, ObjectMapper mapper) {
        this.externalTopic = externalTopic;
        this.mapper = mapper;
    }

    @ConsumeEvent(DATA_ENRICHED_TOPIC)
    Uni<Void> dataEnrichedConsumer(DataEnriched event) {
        return Uni.createFrom()
                  .item(() -> {
                      try {
                          return mapper.writeValueAsString(event);
                      } catch (JsonProcessingException e) {
                          LOGGER.error(String.format("Error occurred mapping DataEnriched event: %s", event.toString()), e);
                          sneakyThrow(e);
                          return null;
                      }
                  })
                  .onItem()
                  .produceCompletionStage(externalTopic::send)
                  .onItem()
                  .invoke(ignore -> LOGGER.info(String.format("%s has been passed to External Bus", event.getClass().getSimpleName())));
    }
}
