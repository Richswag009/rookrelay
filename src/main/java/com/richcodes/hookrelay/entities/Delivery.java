package com.richcodes.hookrelay.entities;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "endpoint_id")
    private Endpoint endpoint;

    @Column(nullable = false)
    private EventStatus eventStatus;

    @Column(nullable = false)
    private int attemptCount =0;

    private Date nextRetryAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    private LocalDateTime updated_at;

    public Delivery() {}

    public Delivery(Long id, Event event, Endpoint endpoint, EventStatus eventStatus, int attemptCount, Date nextRetryAt) {
        this.id = id;
        this.event = event;
        this.endpoint = endpoint;
        this.eventStatus = eventStatus;
        this.attemptCount = attemptCount;
        this.nextRetryAt = nextRetryAt;
    }

    public Delivery(Event event) {
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public Date getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(Date nextRetryAt) {
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


}
