package pl.airq.model.command;

import java.util.List;
import pl.airq.domain.DataProvider;
import pl.airq.model.Payload;

public class EnrichDataPayload implements Payload {

    public final List<DataProvider> providersToEnrich;

    public EnrichDataPayload(List<DataProvider> providersToEnrich) {
        this.providersToEnrich = providersToEnrich;
    }

    @Override
    public String toString() {
        return "EnrichDataPayload{" +
                "providersToEnrich=" + providersToEnrich +
                '}';
    }
}
