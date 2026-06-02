package com.richcodes.hookrelay.response;

import com.richcodes.hookrelay.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.List;

public record DeliveryHistoryResponse(
        String eventId,
        String endpointId,
        DeliveryStatus deliveryStatus,
        int attemptCount,
        List<DeliveryAttemptResponse> attempts

) {

}
