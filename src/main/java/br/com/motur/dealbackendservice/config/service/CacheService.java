package br.com.motur.dealbackendservice.config.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private CacheManager cacheManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    public Object getFromCache(final String cacheName, final String key) {
        final Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            final Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                return valueWrapper.get();
            }
        }

        LOGGER.debug("Cache miss for key: {}", key);
        return null;
    }

    public void putInCache(final String cacheName, final String key, final Object content) {
        try{
            final Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.put(key, content);
                return;
            }
            LOGGER.warn("Cache {} not found", cacheName);
        }
        catch (Exception e){
            LOGGER.error("Error trying to put in cache", e);
        }
    }

    public void removeFromCache(final String cacheName, final String key) {
        final Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            return;
        }
        LOGGER.error("Cache {} not found", cacheName);
    }
}
