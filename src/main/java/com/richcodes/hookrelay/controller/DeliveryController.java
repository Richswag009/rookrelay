package com.richcodes.hookrelay.controller;

import com.richcodes.hookrelay.response.DeliveryResponse;
import com.richcodes.hookrelay.services.deliveries.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;


//    @PostMapping
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public EventResponse createEvent(
//            @RequestBody
//            EventRegisterRequest request) {
//       return  deliveryService.createEvent(request);
//    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<DeliveryResponse> getEvents() {
        return deliveryService.getDeliveries();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public String processDelivery() {
        return deliveryService.processDelivery();
    }
}
