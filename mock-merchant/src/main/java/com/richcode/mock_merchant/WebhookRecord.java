package com.richcode.mock_merchant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "webhook_record")
public class WebhookRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String webhookId;
    private String signature;
    private long timestamp;
    private String payload;  // JSON string, not JsonNode
    private String simulatedStatus;

    @CreationTimestamp
    private LocalDateTime receivedAt;

    public WebhookRecord() {} // required by JPA

    @Override
    public String toString() {
        return "WebhookRecord{" +
                "id='" + id + '\'' +
                ", webhookId='" + webhookId + '\'' +
                ", signature='" + signature + '\'' +
                ", timestamp=" + timestamp +
                ", payload='" + payload + '\'' +
                ", simulatedStatus='" + simulatedStatus + '\'' +
                ", receivedAt=" + receivedAt +
                '}';
    }


    // getters and setters
}