package com.richcode.mock_merchant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebhookService {

    @Autowired
    private WebhookRepository webhookRepository;
    private   final FailureSimulator failureSimulator;

    public WebhookService(FailureSimulator failureSimulator) {
        this.failureSimulator = failureSimulator;
    }

    public ResponseEntity<String> save(WebhookDTO webhookDTO) throws InterruptedException {

        WebhookRecord webhookRecord = new WebhookRecord();
        webhookRecord.setPayload(webhookDTO.payload());
        webhookRecord.setSignature(webhookDTO.signature());
        webhookRecord.setTimestamp(Long.parseLong(webhookDTO.timestamp()));
        webhookRecord.setWebhookId(webhookDTO.webhookID());

        webhookRepository.save(webhookRecord);

        System.out.println("save webhook success" + failureSimulator.simulateFailure());
        return failureSimulator.simulateFailure();
    }

    public List<WebhookRecord> getAllRecords(){
        return webhookRepository.findAll();
    }
}
