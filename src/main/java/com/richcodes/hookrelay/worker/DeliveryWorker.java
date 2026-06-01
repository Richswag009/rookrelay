package com.richcodes.hookrelay.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeliveryWorker {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private DeliveryWorkerService deliveryWorkerService;



    public void pr() {
        System.out.println("Scheduled task executed at " + System.currentTimeMillis());
    }




}
