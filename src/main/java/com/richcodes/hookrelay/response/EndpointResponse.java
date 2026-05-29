package com.richcodes.hookrelay.response;

import com.richcodes.hookrelay.enums.EndpointStatus;

import java.time.LocalDateTime;
import java.util.List;

public record EndpointResponse(
        String id,
        String url,
        String description,
        List<String> events,
        String secret,
        EndpointStatus status,
        LocalDateTime createdAT
) {
}
