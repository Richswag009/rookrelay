package com.richcodes.hookrelay.controller;

import com.richcodes.hookrelay.domain.Delivery;
import com.richcodes.hookrelay.dto.endpoint.StatusRequest;
import com.richcodes.hookrelay.dto.events.EventRegisterRequest;
import com.richcodes.hookrelay.response.DeliveryHistoryResponse;
import com.richcodes.hookrelay.response.EventResponse;
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
    public List<EventResponse> getEvents(
            @RequestParam(required = false) String status) {
        return eventService.getMerchantEvents(status);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}")
    public EventResponse getEventById(
            @PathVariable String id){
        return eventService.getEvent(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{id}")
    public EventResponse updateEventStatusById(
            @PathVariable String id,
            @RequestBody StatusRequest statusRequest){

        return eventService.updateEvent(id, statusRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/deliveries")
    public List<DeliveryHistoryResponse> getEventsDeliveries(@PathVariable String id){
        return eventService.getEventsDeliveriesByEventId(id);
    }
}
