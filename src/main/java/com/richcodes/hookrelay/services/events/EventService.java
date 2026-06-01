package com.richcodes.hookrelay.services.events;

import com.richcodes.hookrelay.dto.events.EventRegisterRequest;
import com.richcodes.hookrelay.response.EventResponse;

import java.util.List;

public interface EventService {
    EventResponse createEvent(EventRegisterRequest request);
    List<EventResponse> getEvents();
}
