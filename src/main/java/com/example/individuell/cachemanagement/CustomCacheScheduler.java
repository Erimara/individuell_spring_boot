package com.example.individuell.cachemanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomCacheScheduler {

    @Autowired
    org.springframework.cache.CacheManager manager;
    @Scheduled(fixedRate = 60000)
    public void evictAllcachesAtIntervals() {
        evictAllCaches();
    }
    public void evictAllCaches() {
        manager.
        manager.getCacheNames().stream()
                .forEach(cacheName -> manager.getCache(cacheName).clear());
    }
}
