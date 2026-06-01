package com.richcodes.hookrelay.dto.events;

import com.fasterxml.jackson.databind.JsonNode;

public record EventRegisterRequest(
        String type,
        JsonNode payload
) {
}
