package com.richcodes.hookrelay.services.events;

import com.richcodes.hookrelay.domain.Delivery;
import com.richcodes.hookrelay.dto.endpoint.StatusRequest;
import com.richcodes.hookrelay.dto.events.EventRegisterRequest;
import com.richcodes.hookrelay.response.DeliveryHistoryResponse;
import com.richcodes.hookrelay.response.EventResponse;

import java.util.List;

public interface EventService {
    EventResponse createEvent(EventRegisterRequest request);
    List<EventResponse> getMerchantEvents(String status);
    EventResponse getEvent(String id);
    EventResponse updateEvent(String id, StatusRequest statusRequest);
    List<DeliveryHistoryResponse> getEventsDeliveriesByEventId(String eventId);
}
