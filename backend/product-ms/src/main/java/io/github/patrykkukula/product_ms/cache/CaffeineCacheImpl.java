package io.github.patrykkukula.product_ms.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.caffeine.CaffeineCache;

@Slf4j
public class CaffeineCacheImpl extends CaffeineCache {
    /**
     * Create a {@link CaffeineCache} instance with the specified name and the
     * given internal {@link Cache} to use.
     *
     * @param name  the name of the cache
     * @param cache the backing Caffeine Cache instance
     */
    public CaffeineCacheImpl(String name, Cache<Object, Object> cache) {
        super(name, cache);
    }

    @Override
    public ValueWrapper get(Object key){
        ValueWrapper vw = super.get(key);
        log.info("[CACHE GET {}] key={} → {}", getName(), key, (vw != null ? "HIT" : "MISS"));
        return vw;
    }

    @Override
    public void evict(Object key) {
        log.info("[CACHE EVICT {}] key={}", getName(), key);
        super.evict(key);
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        log.info("[CACHE PUT {}] key={}", getName(), key);
        super.put(key, toStoreValue(value));
    }
}
