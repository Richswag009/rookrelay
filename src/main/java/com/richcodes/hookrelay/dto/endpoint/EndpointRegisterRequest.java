package com.richcodes.hookrelay.dto.endpoint;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record EndpointRegisterRequest(
        @NotEmpty(message = "url is needed")
        String url,
        String description,
        @NotEmpty(message = "at least one event is needed")
        List<String> events
) {
}
