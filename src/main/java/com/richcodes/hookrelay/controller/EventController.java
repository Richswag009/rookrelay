package com.richcodes.hookrelay.controller;

import com.richcodes.hookrelay.dto.endpoint.EndpointRegisterRequest;
import com.richcodes.hookrelay.dto.events.EventRegisterRequest;
import com.richcodes.hookrelay.response.EndpointResponse;
import com.richcodes.hookrelay.response.EventResponse;
import com.richcodes.hookrelay.services.endpoint.EndpointService;
import com.richcodes.hookrelay.services.events.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;


    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EventResponse createEvent(
            @RequestBody
            EventRegisterRequest request) {
       return  eventService.createEvent(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventResponse> getEvents() {
        return eventService.getEvents();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/deliveries")
    public List<EventResponse> getEventsDeliveries(@PathVariable String id){
        return eventService.getEventsDeliveriesByEventId(id);
    }
}
