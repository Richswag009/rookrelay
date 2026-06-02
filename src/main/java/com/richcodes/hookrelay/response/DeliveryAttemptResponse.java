package com.richcodes.hookrelay.response;

import com.richcodes.hookrelay.domain.Delivery;
import com.richcodes.hookrelay.enums.DeliveryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public record DeliveryAttemptResponse(
        String id,
        String status,
        String httpStatus,
        String responseBody,
        LocalDateTime attemptedAt,
        LocalDateTime nextRetryAt

) {

}
