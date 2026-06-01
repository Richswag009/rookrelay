package com.richcodes.hookrelay.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.richcodes.hookrelay.enums.EventStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;

    @ManyToOne()
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.QUEUED;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column (nullable = false,columnDefinition = "jsonb")
    private JsonNode payload;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    private LocalDateTime updated_at;


    public Event() {}

    public Event(Merchant merchant, String type, EventStatus status, JsonNode payload) {
        this.merchant = merchant;
        this.type = type;
        this.status = status;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }
}
