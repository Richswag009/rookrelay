package com.richcodes.hookrelay.services.events;

import com.richcodes.hookrelay.domain.Delivery;
import com.richcodes.hookrelay.domain.Event;
import com.richcodes.hookrelay.dto.events.EventRegisterRequest;
import com.richcodes.hookrelay.domain.Endpoint;
import com.richcodes.hookrelay.domain.Merchant;
import com.richcodes.hookrelay.enums.DeliveryStatus;
import com.richcodes.hookrelay.enums.EndpointStatus;
import com.richcodes.hookrelay.queue.RedisConfig;
import com.richcodes.hookrelay.repository.DeliveryRepository;
import com.richcodes.hookrelay.repository.EndpointRepository;
import com.richcodes.hookrelay.repository.EventRepository;
import com.richcodes.hookrelay.response.EventResponse;
import com.richcodes.hookrelay.utils.merchant.FindAuthenticatedUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
                savedEvent.getCreatedAt()
        );
    }

    @Override
    public List<EventResponse> getEvents() {
        Merchant merchant = findAuthenticatedUser.findAuthenticatedUser();
        List<Event> endpoints = eventRepository.fetchByMerchant(merchant);
        return endpoints.stream().map( savedEvent -> new EventResponse(
                savedEvent.getId(),
                savedEvent.getType(),
                savedEvent.getStatus(),
                savedEvent.getCreatedAt()
        )).toList();
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

}
