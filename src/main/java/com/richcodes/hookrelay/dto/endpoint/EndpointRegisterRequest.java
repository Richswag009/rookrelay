package com.richcodes.hookrelay.dto.endpoint;

import java.util.List;

public record EndpointRegisterRequest(
        String url,
        String description,
        List<String> events
) {
}
