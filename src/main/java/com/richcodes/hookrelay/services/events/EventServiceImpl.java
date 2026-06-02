package com.richcodes.hookrelay.services.events;

import com.richcodes.hookrelay.domain.*;
import com.richcodes.hookrelay.dto.endpoint.StatusRequest;
import com.richcodes.hookrelay.dto.events.EventRegisterRequest;
import com.richcodes.hookrelay.enums.DeliveryStatus;
import com.richcodes.hookrelay.enums.EndpointStatus;
import com.richcodes.hookrelay.enums.EventStatus;
import com.richcodes.hookrelay.queue.RedisConfig;
import com.richcodes.hookrelay.repository.DeliveryRepository;
import com.richcodes.hookrelay.repository.EndpointRepository;
import com.richcodes.hookrelay.repository.EventRepository;
import com.richcodes.hookrelay.response.DeliveryAttemptResponse;
import com.richcodes.hookrelay.response.DeliveryHistoryResponse;
import com.richcodes.hookrelay.response.EventResponse;
import com.richcodes.hookrelay.utils.merchant.FindAuthenticatedUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final FindAuthenticatedUser findAuthenticatedUser;
    @Autowired
    private final EndpointRepository endpointRepository;

    @Autowired
    private final DeliveryRepository deliveryRepository;
    @Autowired
    private final RedisTemplate<String,String> redisTemplate;

    public static final String DELIVERY_QUEUE = "hookrelay:delivery:queue";


    public EventServiceImpl(EventRepository eventRepository, FindAuthenticatedUser findAuthenticatedUser, EndpointRepository endpointRepository, RedisConfig redisConfig, DeliveryRepository deliveryRepository) {
        this.eventRepository = eventRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.endpointRepository = endpointRepository;
        this.deliveryRepository = deliveryRepository;
        this.redisTemplate = new RedisTemplate<>();
    }

    @Override
    @Transactional()
    public EventResponse createEvent(EventRegisterRequest request) {
        Merchant merchant = findAuthenticatedUser.findAuthenticatedUser();

        List<Endpoint> endpoints = getEndpointsByEvent(
                merchant, request.type(), EndpointStatus.ACTIVE);

        Event event = new Event();
        event.setMerchant(merchant);
        event.setType(request.type());
        event.setPayload(request.payload());
        Event savedEvent = eventRepository.save(event);

        for (Endpoint endpoint : endpoints) {

            Delivery delivery = new Delivery();
            delivery.setEndpoint(endpoint);
            delivery.setEvent(event);
            delivery.setDeliveryStatus(DeliveryStatus.QUEUED);
            Delivery savedDelivery = deliveryRepository.save(delivery);

            publishEvent(savedDelivery);
        }

        return  new EventResponse(
                savedEvent.getId(),
                savedEvent.getType(),
                savedEvent.getStatus(),
                savedEvent.getCreatedAt(),
                List.of()
        );
    }

    @Override
    public List<EventResponse> getMerchantEvents(String status) {
        Merchant merchant = findAuthenticatedUser.findAuthenticatedUser();
        List<Event> endpoints = eventRepository.fetchByMerchant(merchant);

        return endpoints.stream().map( savedEvent -> new EventResponse(
                savedEvent.getId(),
                savedEvent.getType(),
                savedEvent.getStatus(),
                savedEvent.getCreatedAt(),
                List.of()
        )).toList();
    }

    @Override
    public EventResponse getEvent(String id) {
        Event event = findMerchantEventById(id);
        return new EventResponse(
             event.getId(),
             event.getType(),
             event.getStatus(),
             event.getCreatedAt(),
                List.of()
        );
    }

    @Override
    public EventResponse updateEvent(String id, StatusRequest statusRequest) {
        Event event = findMerchantEventById(id);

        EventStatus eventStatus = EventStatus.valueOf(statusRequest.status());
        event.setStatus(eventStatus);

        Event updatedEvent = eventRepository.save(event);

        return new EventResponse(
                updatedEvent.getId(),
                updatedEvent.getType(),
                updatedEvent.getStatus(),
                updatedEvent.getCreatedAt(),
                List.of()
        );
    }


    @Override
    public List<DeliveryHistoryResponse> getEventsDeliveriesByEventId(String eventId) {
        Merchant merchant = findAuthenticatedUser.findAuthenticatedUser();
        List<Delivery> delivery = deliveryRepository.findDeliveriesByEventIdAndMerchant(eventId, merchant);
        return  delivery.stream().map(
                savedDelivery -> new DeliveryHistoryResponse(
                        savedDelivery.getEvent().getId(),
                        savedDelivery.getEndpoint().getId(),
                        savedDelivery.getDeliveryStatus(),
                        savedDelivery.getAttemptCount(),
                        convertDeliveryAttemptResponse(savedDelivery.getDeliveryAttempts(),savedDelivery)
                )
        ).toList();
    }

    public List<Endpoint> getEndpointsByEvent(
            Merchant merchant,
            String eventType,
            EndpointStatus eventStatus
    ) {
        return endpointRepository
                .findByMerchantAndSubscribedEvent(merchant, eventType,eventStatus);
    }

    public void publishEvent(Delivery delivery) {

        redisTemplate.opsForList()
                .leftPush(DELIVERY_QUEUE, delivery.getId().toString());

    }


    private Event findMerchantEventById(String id) {

        Merchant merchant = findAuthenticatedUser.findAuthenticatedUser();
        Optional<Event> event= eventRepository.findByIdAndMerchant(merchant,id);

        if(event.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Endpoint not found");
        }

        return event.get();
    }

    private List<DeliveryAttemptResponse> convertDeliveryAttemptResponse(
            List<DeliveryAttempt> attempts, Delivery delivery) {

        return attempts.stream().map(attempt ->
                new DeliveryAttemptResponse(
                        attempt.getId(),
                        attempt.getStatus().name(),
                        attempt.getHttpStatus(),
                        attempt.getResponseBody(),
                        attempt.getAttemptedAt(),
                        delivery.getNextRetryAt()
                )
        ).toList();
    }

}
