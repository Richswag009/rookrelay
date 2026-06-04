package com.richcode.mock_merchant;

import org.hibernate.id.AbstractUUIDGenerator;

public record WebhookDTO(
        String payload,
        String signature,
        String timestamp,
        String webhookID

) {
}
