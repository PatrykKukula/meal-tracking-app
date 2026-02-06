package io.github.patrykkukula.product_ms.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager() {
            @Override
            protected Cache createCaffeineCache(String name) {
                com.github.benmanes.caffeine.cache.Cache<Object, Object> cache = cacheBuilder().build();
                return new CaffeineCacheImpl(name, cache);
            }
        };
        return cacheManager;
    }

    private Caffeine<Object, Object> cacheBuilder() {
                return Caffeine.newBuilder()
                        .initialCapacity(1000)
                        .maximumSize(2000)
                        .expireAfterAccess(Duration.ofHours(2)).recordStats();
            }
}
