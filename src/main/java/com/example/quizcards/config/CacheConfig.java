package com.example.quizcards.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager mgr = new SimpleCacheManager();

        // cache tokenMeta: hết hạn sau 1 phút
        CaffeineCache tokenMeta = new CaffeineCache(
                "tokenMeta",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.MINUTES)
                        .maximumSize(10_000)
                        .build()
        );

        // cache khác: hết hạn sau 5 phút
        CaffeineCache setPassInfo = new CaffeineCache(
                "setPassInfo",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(10_000)
                        .build()
        );
        CaffeineCache validatedSets = new CaffeineCache(
                "validatedSets",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(10_000)
                        .build()
        );
        CaffeineCache textLangDetect = new CaffeineCache(
                "textLangDetectForSpeech",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(10_000)
                        .build()
        );

        mgr.setCaches(List.of(tokenMeta, setPassInfo, validatedSets, textLangDetect));
        return mgr;
    }

//    @Bean
//    public CaffeineCacheManager caffeineCacheManager() {
//        CaffeineCacheManager mgr = new CaffeineCacheManager(
//                "tokenMeta",
//                "setPassInfo",
//                "validatedSets",
//                "textLangDetectForSpeech"
//        );
//        mgr.setCaffeine(Caffeine.newBuilder()
//                .expireAfterWrite(5, TimeUnit.MINUTES)
//                .maximumSize(10_000)
//        );
//
//        return mgr;
//    }
}