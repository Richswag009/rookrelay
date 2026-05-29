package com.richcodes.hookrelay.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record MerchantRegisterResponse(
        String id,
        String name,
        String email,
        String apiKey,
        UUID merchantId,
        LocalDateTime createdAT
) {
}
