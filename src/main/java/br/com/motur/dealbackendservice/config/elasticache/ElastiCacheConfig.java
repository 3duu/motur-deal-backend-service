package br.com.motur.dealbackendservice.config.elasticache;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import java.time.Duration;

@Configuration
//@IcarrosDependecyTree( elasticache = @Elasticache(nome = "test-ms-anunciocache.icarros.io",
        //descricao = "Elasticache de anuncio."))
public class ElastiCacheConfig {

    final RedisValueSerializer serializer = new RedisValueSerializer();

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("ICARROSCONFIG_APPLICATION",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(4)).disableCachingNullValues()).enableStatistics()
                .withCacheConfiguration("ICARROSCONFIG_FIPE",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(48)).disableCachingNullValues()).enableStatistics()
                .withCacheConfiguration("ICARROSCONFIG",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)).disableCachingNullValues()).enableStatistics()
                .withCacheConfiguration("COTACAOPRECO",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(24)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))).enableStatistics()
                .withCacheConfiguration("VERSAO",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(7)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))).enableStatistics()
                .withCacheConfiguration("ANUNCIANTERESUMIDO",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)).disableCachingNullValues()).enableStatistics()
                .withCacheConfiguration("CIDADEENTITY",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(60)).disableCachingNullValues()).enableStatistics()
                .withCacheConfiguration("KEYCLOAK_TOKEN",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(280)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))).enableStatistics();
    }
}
