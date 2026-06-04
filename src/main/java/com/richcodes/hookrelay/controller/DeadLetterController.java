package com.richcodes.hookrelay.controller;

import com.richcodes.hookrelay.response.DeliveryResponse;
import com.richcodes.hookrelay.services.deliveries.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/dead-letter")
public class DeadLetterController {

    @Autowired
    private DeliveryService deliveryService;



    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<DeliveryResponse> getDeadDeliveries() {
        return deliveryService.getDeadDeliveries();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/replay")
    public void replayDeliveryById(@PathVariable String id) {
        deliveryService.replayDelivery(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/dismiss")
    public void dismissDeliveryById(@PathVariable String id) {
        deliveryService.dismissDeliveryById(id);
    }

}
