package io.github.patrykkukula.diet_ms.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.patrykkukula.mealtrackingapp_common.cache.CaffeineCacheImpl;
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
        return new CaffeineCacheManager() {
            @Override
            protected Cache createCaffeineCache(String name) {
                com.github.benmanes.caffeine.cache.Cache<Object, Object> cache = cacheBuilder(name).build();
                return new CaffeineCacheImpl(name, cache);
            }
        };
    }

    private Caffeine<Object, Object> cacheBuilder(String name) {
        switch (name) {
            case "dietDay" -> {
                return Caffeine.newBuilder()
                        .initialCapacity(1000)
                        .maximumSize(2000)
                        .expireAfterAccess(Duration.ofHours(2)).recordStats();
            }
            case "monthlyDiets" -> {
                return Caffeine.newBuilder()
                        .initialCapacity(500)
                        .maximumSize(1000)
                        .expireAfterAccess(Duration.ofHours(2)).recordStats();
            }
            default  -> {
                return Caffeine.newBuilder()
                        .initialCapacity(100)
                        .maximumSize(100)
                        .expireAfterAccess(Duration.ofMinutes(15)).recordStats();
            }
        }
    }
}
