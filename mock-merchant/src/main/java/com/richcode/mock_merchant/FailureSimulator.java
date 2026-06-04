package com.richcode.mock_merchant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class FailureSimulator {

    @Value("${merchant.failure.rate:0.4}")
    private double failureRate;

    @Value("${merchant.timeout.rate:0.1}")
    private double timeoutRate;

    @Value("${merchant.slow.rate:0.2}")
    private double slowRate;

    public ResponseEntity<String> simulateFailure() throws InterruptedException {
        double random = Math.random();

        if (random < timeoutRate) {
            Thread.sleep(60000);
        } else if (random < failureRate) {
            return ResponseEntity.status(500).body("Simulated failure");
        }

        return ResponseEntity.ok("Webhook received");
    }

}
