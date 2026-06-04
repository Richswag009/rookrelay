package com.richcode.mock_merchant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("webhook")
public class WebhookController {


    private final WebhookRepository webhookRepository;
    private final WebhookService webhookService;

    public WebhookController(WebhookRepository webhookRepository, WebhookService webhookService) {

        this.webhookRepository = webhookRepository;
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<String> receiveWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Webhook-Signature") String signature,
            @RequestHeader("X-Webhook-Timestamp") String timestamp,
            @RequestHeader("X-Webhook-ID") String webhookId) throws InterruptedException {

        WebhookDTO webhookDTO = new WebhookDTO(
                payload,signature,timestamp,webhookId
        );
       return webhookService.save(webhookDTO);
    }

    @GetMapping
    public List<WebhookRecord> findAll() {
       return webhookRepository.findAll();
    }

    @GetMapping("health")
    public String getHealth() {
          return "its working ";
    }
}
