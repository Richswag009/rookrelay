package com.richcodes.hookrelay.domain;

import com.richcodes.hookrelay.enums.DeliveryStatus;
import com.richcodes.hookrelay.enums.EventStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity()
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "endpoint_id")
    private Endpoint endpoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.QUEUED;

    @Column(nullable = false)
    private int attemptCount =0;

    private LocalDateTime nextRetryAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    private LocalDateTime updated_at;

    public Delivery() {}

    public Delivery(String id, Event event, Endpoint endpoint, DeliveryStatus deliveryStatus, int attemptCount, LocalDateTime nextRetryAt) {
        this.id = id;
        this.event = event;
        this.endpoint = endpoint;
        this.status = deliveryStatus;
        this.attemptCount = attemptCount;
        this.nextRetryAt = nextRetryAt;
    }

    public Delivery(Event event) {
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DeliveryStatus getDeliveryStatus() {
        return status;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.status = deliveryStatus;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public LocalDateTime getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(LocalDateTime nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }


    public LocalDateTime getCreated_at() {
        return created_at;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id='" + id + '\'' +
                ", event=" + event +
                ", endpoint=" + endpoint +
                ", status=" + status +
                ", attemptCount=" + attemptCount +
                ", nextRetryAt=" + nextRetryAt +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
