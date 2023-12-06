package com.example.individuell.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Component that has a fixed schedule to check redis keys every 6 seconds.
 * If a users logged-in duration has expired the function will delete that key and logout the user
 */
@Component
public class SessionManager {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public SessionManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Schedule method which ticks ever 6 sec.
     * the relevant keys regarding the users login-session is retrieved.
     * Null checks are then performed to ensure that the program does not crash.
     * To be able to delete the session that keeps the user logged in the duration key is looped through,
     * we can then parse this information to a long to see if the session-duration is expired
     * If it has expired the main login-session is deleted
     */
    @Scheduled(fixedDelay = 6000)
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

    /**
     * Method for deleting keys
     * @param sKey represents the sessionKey
     * @param dKey represents the durationKey
     */
    private void deleteKeys(String sKey, String dKey) {
        redisTemplate.delete(sKey);
        redisTemplate.delete(dKey);
    }
}


