package com.richcodes.hookrelay.worker;


import com.fasterxml.jackson.databind.JsonNode;
import com.richcodes.hookrelay.domain.Delivery;
import com.richcodes.hookrelay.domain.DeliveryAttempt;
import com.richcodes.hookrelay.enums.DeliveryStatus;
import com.richcodes.hookrelay.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class DeliveryWorkerService {

    @Autowired
    private  RedisTemplate<String,String> redisTemplate;
    @Autowired
    private DeliveryRepository deliveryRepository;


    private final RetryPolicy retryPolicy;

    @Autowired
    private RestTemplate restTemplate;

    private final ExecutorService executor =
            Executors.newVirtualThreadPerTaskExecutor();


    public static final String DELIVERY_QUEUE = "hookrelay:delivery:queue";

    public DeliveryWorkerService(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }


    @Scheduled(fixedRate = 10000)
    public void delivery() {
        try{
//            while (true) {
////                String deliveryId = String.valueOf(redisTemplate.opsForList()
////                        .rightPop(DELIVERY_QUEUE,0));
//                String deliveryIds =  redisTemplate.opsForList().index(DELIVERY_QUEUE, 0);
//                executor.submit(() -> processDelivery(deliveryIds));
//            }

            List<String> deliveryIds = redisTemplate.opsForList()
                    .range(DELIVERY_QUEUE, 0, -1);

            for (String id : deliveryIds) {
                executor.submit(() -> processDelivery(id));
            }
        }catch (Exception e){
            System.out.println("delivery failed: " + e.getMessage());
        }
    }

    public String processDelivery(String deliveryId) throws Exception {
        Optional<Delivery> delivery = deliveryRepository.findByIdWithDetails(deliveryId);

        JsonNode payload = delivery.get().getEvent().getPayload();
        String url = delivery.get().getEndpoint().getUrl();

        return retryPolicy.retryPolicy(() -> sendWebhook(url, payload,delivery.orElse(null)));
    }

    public String sendWebhook(String webhookUrl, JsonNode payload,Delivery delivery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", "dsgsg");

        HttpEntity<JsonNode> request = new HttpEntity<>(payload, headers);

        // 4. Send POST request
        ResponseEntity<String> response = null;
//        String url = "https://httpbin.org/delay/10 ";
        String url2 = "https://webhook.site/a5884ad2-e54f-4b85-b3e0-909b7a6bfc61";
        try {
            response =
                    restTemplate.postForEntity(
                            url2,
                            request,
                            String.class
                    );
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("SUCCESS: " + response.getStatusCode());
                updateDeliveryStatus(delivery);
                return response.getBody();
            }

            System.out.println("FAILED HTTP: " + response.getStatusCode());
            return response.getBody();
        }catch (Exception e) {
            System.out.println("Webhook failed: " + e.getMessage());
            return null; // or return a failure object
        }

    }

    private void updateDeliveryStatus(Delivery delivery) {
        delivery.setDeliveryStatus(DeliveryStatus.SUCCESSFUL);
        deliveryRepository.save(delivery);
    }

    private void createDeliveryAttempt(Delivery delivery,ResponseEntity<String> response ) {


        delivery.setDeliveryStatus(DeliveryStatus.FAILED);
        delivery.setAttemptCount(delivery.getAttemptCount() + 1);
        delivery.setNextRetryAt(calculateNextRetry(delivery.getAttemptCount()));
        deliveryRepository.save(delivery);


        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.setDelivery(delivery);
        attempt.setHttpStatus(response.getStatusCode().toString());
        attempt.setResponseBody(response.getBody());
        attempt.setAttemptedAt(LocalDateTime.now());
        deliveryAttemptRepository.save(attempt);

    }

    private LocalDateTime calculateNextRetry(int attemptCount) {

        return  switch (attemptCount){
            case 1 -> LocalDateTime.now().plusSeconds(30);
            case 2 -> LocalDateTime.now().plusMinutes(5);
            case 3 -> LocalDateTime.now().plusMinutes(30);
            case 4 -> LocalDateTime.now().plusHours(2);
            case 5 -> LocalDateTime.now().plusHours(5);
            default -> LocalDateTime.now();
        };
    }

}
