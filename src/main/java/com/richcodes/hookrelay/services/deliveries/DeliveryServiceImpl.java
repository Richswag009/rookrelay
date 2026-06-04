package com.richcodes.hookrelay.services.deliveries;


import com.fasterxml.jackson.databind.JsonNode;
import com.richcodes.hookrelay.domain.Delivery;
import com.richcodes.hookrelay.enums.DeliveryStatus;
import com.richcodes.hookrelay.repository.DeliveryRepository;
import com.richcodes.hookrelay.response.DeliveryResponse;
import com.richcodes.hookrelay.response.EndpointResponse;
import com.richcodes.hookrelay.response.EventResponse;
import com.richcodes.hookrelay.worker.DeliveryWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.richcodes.hookrelay.worker.DeliveryWorkerService.DELIVERY_QUEUE;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private DeliveryWorkerService deliveryWorkerService;

    @Override
    public List<DeliveryResponse> getDeliveries() {
       List<Delivery> deliveries = deliveryRepository.findAllWithRelations();
        return deliveries.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public Optional<Delivery> getDelivery(String id) {
        Optional<Delivery> delivery = deliveryRepository.findByIdWithDetails(id);
        return Optional.of(delivery.get());
    }

    public String processDelivery() {
        String deliveryIds =  redisTemplate.opsForList().index(DELIVERY_QUEUE, 0);

        Optional<Delivery>   delivery = Optional.of(deliveryRepository.findByIdWithDetails(deliveryIds).get());

        JsonNode payload = delivery.get().getEvent().getPayload();
        String url = delivery.get().getEndpoint().getUrl();

        return deliveryWorkerService.sendWebhook(url, payload,delivery.get());
    }

    @Override
    public List<DeliveryResponse> getDeadDeliveries() {
        List<Delivery> deliveries = deliveryRepository.findAllDeadLettersWithRelations(DeliveryStatus.DEAD_LETTER);
        return deliveries.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void replayDelivery(String id) {
        Delivery delivery = deliveryRepository.findDeadLettersByIdWithRelations(
                DeliveryStatus.DEAD_LETTER, id);

        if (delivery == null) {
            throw new RuntimeException("Invalid delivery id: " + id);
        }

        delivery.setAttemptCount(0);
        delivery.setDeliveryStatus(DeliveryStatus.QUEUED);
        delivery.setNextRetryAt(null);
        deliveryRepository.save(delivery);

        redisTemplate.opsForList()
                .leftPush(DELIVERY_QUEUE, delivery.getId().toString());
    }

    @Override
    public void dismissDeliveryById(String id) {
        Delivery delivery = deliveryRepository.findDeadLettersByIdWithRelations(
                DeliveryStatus.DEAD_LETTER, id);

        if (delivery == null) {
            throw new RuntimeException("Invalid delivery id: " + id);
        }

        delivery.setDeliveryStatus(DeliveryStatus.DISMISSED);
        deliveryRepository.save(delivery);
    }


    private DeliveryResponse toResponse(Delivery delivery) {

        return new DeliveryResponse(
                delivery.getId(),
                delivery.getDeliveryStatus(),
                delivery.getAttemptCount(),
                delivery.getCreated_at(),
                delivery.getNextRetryAt(),

                new EventResponse(
                        delivery.getEvent().getId(),
                        delivery.getEvent().getType(),
                        delivery.getEvent().getStatus(),
                        delivery.getEvent().getCreatedAt(),
                        List.of()
                ),
                new EndpointResponse(
                        delivery.getEndpoint().getId(),
                        delivery.getEndpoint().getUrl(),
                        delivery.getEndpoint().getDescription(),
                        delivery.getEndpoint().getSubscribedEvents(),
                        "",
                        delivery.getEndpoint().getStatus(),
                        delivery.getEndpoint().getCreated_at()
                )
        );

    }


}
