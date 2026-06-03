package com.richcodes.hookrelay.signing;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

public class WebhookSignature {


    public static String generateHMACSignature(String secret, String timestamp, String payload)
            throws NoSuchAlgorithmException, InvalidKeyException {

        String signedContent = timestamp + "." + payload;
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] signatureBytes = hmacSHA256.doFinal(signedContent.getBytes());
        return "sha256=" + HexFormat.of().formatHex(signatureBytes);
    }

}
