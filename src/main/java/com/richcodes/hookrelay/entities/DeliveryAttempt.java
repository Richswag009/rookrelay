package com.richcodes.hookrelay.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name="delivery_attempts")
public class DeliveryAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne( fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Column(nullable = false)
    private String httpStatus;

    @Column(nullable = false)
    private String responseBody;

    @CreationTimestamp
    @Column(nullable = false)
    private Date attemptedAt;

    @Column(nullable = false)
    private double duration_ms;

    public DeliveryAttempt() {}

    public DeliveryAttempt(Delivery delivery, String httpStatus, String responseBody) {
        this.delivery = delivery;
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Date getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(Date attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public double getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(double duration_ms) {
        this.duration_ms = duration_ms;
    }
}
