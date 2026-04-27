package io.github.patrykkukula.diet_ms.cache;

import io.github.patrykkukula.diet_ms.model.DietDay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheUtils {
    private final CacheManager cacheManager;
    private static final String MONTHLY_DIETS = "monthlyDiets";
    private static final String DIET_DAY = "dietDay";

    public void evictMonthlyDietsCache(DietDay dietDay) {
        String key = ("%s-%s-%s").formatted(
                dietDay.getDate().getYear(),
                dietDay.getDate().getMonthValue(),
                dietDay.getOwnerUsername()
        );

        Cache cache = cacheManager.getCache(MONTHLY_DIETS);
        if (cache != null) {
            cache.evict(key);
        }
        else {
            log.info("[CACHE {} does not exists", MONTHLY_DIETS);
        }
    }

    public void evictDietDayCache(DietDay dietDay) {
        String key = dietDay.getDietDayId() + "-" + dietDay.getOwnerUsername();
        Cache cache = cacheManager.getCache(DIET_DAY);
        if (cache != null) {
            cache.evict(dietDay.getDietDayId());
        }
        else {
            log.info("[CACHE {} does not exists", MONTHLY_DIETS);
        }
    }

    public void evictCaches(DietDay dietDay) {
        this.evictDietDayCache(dietDay);
        this.evictMonthlyDietsCache(dietDay);
    }
}
