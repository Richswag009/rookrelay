package com.richcodes.hookrelay.worker;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Set;


@Component
public class RetryPolicy {
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_BASE_TIMEOUT = 37000; // milliseconds
    private  static  final  int DEFAULT_BACKOFF_FACTOR = 2;
    private final int maxRetries;
    private final int baseTimeout;
    private final int backoffFactor;
    private static final Set<String> RETRYABLE_CODES = Set.of("429", "503", "504");

    public RetryPolicy() {
        this.maxRetries = DEFAULT_MAX_RETRIES;
        this.baseTimeout = DEFAULT_BASE_TIMEOUT;
        this.backoffFactor = DEFAULT_BACKOFF_FACTOR;
    }



    RetryPolicy(int maxRetries, int baseTimeout, int backoffFactor) {
        if(maxRetries < 1){
            throw new IllegalArgumentException("maxRetries must be greater than 0");
        }
        this.maxRetries=maxRetries;
        this.baseTimeout =baseTimeout;
        this.backoffFactor = backoffFactor;
    }



    public <T> T  retryPolicy(ThrowingSupplier<T> action) throws Exception {
        int retries = 0;

        while (retries < maxRetries) {
            try {
                System.out.println("making request...");
                return action.get();

            }catch (Exception e) {
                if (!isRetryable(e)) {
                    throw e;
                }
                System.out.println("Attempt " + (retries + 1) + " of " + maxRetries + " failed");
                retries++;

                if(retries < maxRetries) {
                    int backoff = calculateBackOffTime(retries);
                    System.out.println( "Please retry in " + backoff + " milliseconds" );
                    Thread.sleep(backoff);
                }else{
                    System.out.println("Max retries reached");
                    throw e;
                }
            }
        }

        return null;
    }

    private int calculateBackOffTime(int retries){
        return (int) (baseTimeout * Math.pow(backoffFactor,retries));
    }

    private boolean isRetryable(Exception e) {

        // 1. TIMEOUT / NETWORK ISSUES
        if (e instanceof ResourceAccessException) {

            System.out.println(e.getMessage());
            return true;
        }

        // 2. 4xx errors
        if (e instanceof HttpClientErrorException ex) {
            HttpStatusCode status = ex.getStatusCode();

            // retry only rate limiting
            return status.value() == 429;
        }

        // 3. 5xx errors
        if (e instanceof HttpServerErrorException) {
            return true;
        }

        // 4. fallback
        return false;
    }
}
