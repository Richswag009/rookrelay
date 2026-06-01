package com.richcodes.hookrelay.services.deliveries;

import com.richcodes.hookrelay.domain.Delivery;
import com.richcodes.hookrelay.response.DeliveryResponse;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryService {

    @Transactional(readOnly = true)
    List<DeliveryResponse> getDeliveries();
    Optional<Delivery> getDelivery(String id);
    String processDelivery();
}
