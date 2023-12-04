package com.example.individuell.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SessionManager {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public SessionManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedDelay = 6000)
    public void checkSession() {
        deleteSession();
    }

    public void deleteSession() {
        Set<String> sessionKeys = redisTemplate.keys("spring:session:doogle*");
        Set<String> durationKeys = redisTemplate.keys("duration*");

        if (sessionKeys == null || durationKeys == null) {
            return;
        }
        for (String sKey : sessionKeys) {
            for (String dKey : durationKeys) {
                String duration = redisTemplate.opsForValue().get(dKey);

                if (duration == null) {
                    return;
                }
                long parsedDurationInMillis = Long.parseLong(duration);

                if (System.currentTimeMillis() > parsedDurationInMillis) {
                    System.out.println(sKey + " has expired at the time of " + System.currentTimeMillis());
                    deleteKeys(sKey, dKey);
                }
            }
        }
    }
    private void deleteKeys(String sKey, String dKey) {
        redisTemplate.delete(sKey);
        redisTemplate.delete(dKey);
    }
}


