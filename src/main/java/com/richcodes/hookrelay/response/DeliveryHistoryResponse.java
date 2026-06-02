package com.richcodes.hookrelay.response;

import java.time.LocalDateTime;
import java.util.List;

public record DeliveryHistoryResponse(
        String eventId,
        String endpointId,
        List<DeliveryAttemptResponse> attempts

) {

}
