package com.richcodes.hookrelay.response;

import java.time.LocalDateTime;

public record MerchantRegisterResponse(
        String id,
        String name,
        String email,
        String apiKey,
        LocalDateTime createdAT
) {
}
