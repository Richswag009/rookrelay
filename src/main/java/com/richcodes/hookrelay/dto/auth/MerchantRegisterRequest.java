package com.richcodes.hookrelay.dto.auth;

public record MerchantRegisterRequest(
        String name,
        String phone,
        String email
) {
}
