package com.richcodes.hookrelay.utils.secret;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;


@Component
public class WebhookSecretGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateSecret() {
        byte[] bytes = new byte[32]; // 256-bit secret
        secureRandom.nextBytes(bytes);

        return "rookrelay" + Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }


}
