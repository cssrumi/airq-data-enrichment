package pl.airq.enrichment.domain;

import org.apache.commons.lang3.BooleanUtils;

public enum Status {

    FAILURE, SUCCESS;

    public static Status status(Boolean result) {
        return BooleanUtils.isNotTrue(result) ? FAILURE : SUCCESS;
    }
}
