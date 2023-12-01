package com.example.individuell.session;

import com.example.individuell.security.Hash;
import org.bson.types.BSONTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ScheduleSessionCheck {

private final StringRedisTemplate redisTemplate;

    @Autowired
    public ScheduleSessionCheck(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedDelay = 6000)
    public void checkSession(){
        Set<String> sessionKeys = redisTemplate.keys("spring:session:doogle*");
        Set<String> durationKeys = redisTemplate.keys("duration*");

        if (sessionKeys != null && durationKeys != null){
            for (String sKey : sessionKeys){
                for (String dKey : durationKeys){
                    String duration = redisTemplate.opsForValue().get(dKey);
                    if (duration != null) {
                        Long parsedDurationInMillis = Long.parseLong(duration);
                        if (System.currentTimeMillis() > parsedDurationInMillis) {
                            System.out.println(sKey + " has expired at the time of " + System.currentTimeMillis());
                            redisTemplate.delete(sKey);
                            redisTemplate.delete(dKey);
                        }
                    }
                }

            }

            }
        }

        /*System.out.println(redisTemplate.opsForValue().getAndExpire("user", "value");*/
    }


