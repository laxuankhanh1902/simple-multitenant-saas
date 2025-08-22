package io.conduktor.saas.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Value("${app.cache.tenant-cache-size:100}")
    private int tenantCacheSize;

    @Value("${app.cache.user-cache-size:1000}")
    private int userCacheSize;

    @Value("${app.cache.token-cache-size:5000}")
    private int tokenCacheSize;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .recordStats());
        
        cacheManager.setCacheNames(java.util.List.of(
                "tenants", 
                "users", 
                "tokens", 
                "kafka-clusters", 
                "kafka-topics", 
                "projects",
                "subscriptions"
        ));
        
        return cacheManager;
    }

    @Bean("tenantCache")
    public Caffeine<Object, Object> tenantCaffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(tenantCacheSize)
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats();
    }

    @Bean("userCache")
    public Caffeine<Object, Object> userCaffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(userCacheSize)
                .expireAfterWrite(Duration.ofMinutes(15))
                .recordStats();
    }

    @Bean("tokenCache")
    public Caffeine<Object, Object> tokenCaffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(tokenCacheSize)
                .expireAfterWrite(Duration.ofMinutes(60))
                .recordStats();
    }
}