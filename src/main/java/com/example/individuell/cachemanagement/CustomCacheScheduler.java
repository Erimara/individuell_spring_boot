package com.example.individuell.cachemanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a class for handling a cache evict scheduler. Every 1 min it evicts all the cached material in redis.
 */
@RestController
public class CustomCacheScheduler {

    @Autowired
    CacheManager manager;

    /**
     * Method which takes in another helper method which clears the cache that is saved in redis.
     * Has a schedule rate of ever 1 min.
     */
    @Scheduled(fixedRate = 60000)
    public void evictAllcachesAtIntervals() {
        evictAllCaches();
    }
    public void evictAllCaches() {
        manager.getCacheNames().stream()
                .forEach(cacheName -> manager.getCache(cacheName).clear());
    }
}
