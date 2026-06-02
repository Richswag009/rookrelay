package com.richcodes.hookrelay.dto.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.richcodes.hookrelay.enums.EndpointStatus;

public record StatusRequest(
        @JsonProperty("status")
        String status
) {
}
