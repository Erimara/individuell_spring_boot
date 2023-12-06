package com.example.individuell.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import java.time.Duration;

/**
 * This is a configuration class for setting up the redis connection to spring boot
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * To use spring boot and redis together we use a LettuceConnectionFactory which handles the connection between them
     * @return RedisConnectionfactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host,port);
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfig);
        System.out.println("Redis-connection : " + host + ":" + port);
        return connectionFactory;
    }

    /**
     * To be able to store cached data in redis we need a bean which configures a ttl and the connection to Redis
     * @param connectionFactory Is a built in spring method which creates a connection to redis
     * @return RedisCacheManager, which allows us to handle caching.
     */
    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .build();
    }

}
