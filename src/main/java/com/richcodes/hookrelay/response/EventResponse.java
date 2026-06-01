package com.richcodes.hookrelay.response;

import com.richcodes.hookrelay.enums.EndpointStatus;
import com.richcodes.hookrelay.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public record EventResponse(
        String id,
        String type,
        EventStatus status,
        LocalDateTime createdAT
) {
}
