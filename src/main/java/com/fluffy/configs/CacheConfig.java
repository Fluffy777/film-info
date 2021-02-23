package com.fluffy.configs;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Клас, що відповідає за конфігурацію кешування в додатку.
 * @author Сивоконь Вадим
 */
@Configuration
@EnableCaching
public class CacheConfig {
    /**
     * Повертає бін, що здійснює керування кешуванням.
     * @return бін, що керує кешуванням
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("responseBodies", "documents");
    }
}
