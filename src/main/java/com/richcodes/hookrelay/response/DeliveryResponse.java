package com.richcodes.hookrelay.response;

import com.richcodes.hookrelay.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.Date;

public record DeliveryResponse(
        String id,
        DeliveryStatus status,
        int attemptCount,
        LocalDateTime createdAt,
        LocalDateTime nextRetryAt,

        EventResponse event,
        EndpointResponse endpoint
) {

}
