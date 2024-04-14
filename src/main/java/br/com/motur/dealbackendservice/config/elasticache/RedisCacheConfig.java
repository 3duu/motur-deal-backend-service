package br.com.motur.dealbackendservice.config.elasticache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import java.time.Duration;
import br.com.motur.dealbackendservice.core.model.common.CacheNames;

import static io.lettuce.core.ReadFrom.MASTER;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private final String host;
    private final int port;

    final RedisValueSerializer serializer = new RedisValueSerializer();

    public RedisCacheConfig(@Value("${spring.data.redis.host}") String host, @Value ("${spring.data.redis.port}") int port) {
        this.host = host;
        this.port = port;
    }


    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration(CacheNames.COLORS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(4)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))).enableStatistics()
                .withCacheConfiguration(CacheNames.PROVIDER_CATALOG,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(3)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))).enableStatistics()
                .withCacheConfiguration(CacheNames.FIND_BY_CATEGORY,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(48)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))).enableStatistics();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        final LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(MASTER)
                .build();

        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port), clientConfig);
    }

    @Bean
    public RedisCacheManager cacheManager(final RedisConnectionFactory connectionFactory) {

        final RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        final RedisCacheManager cm = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration)
                //.withInitialCacheConfigurations(Map.of("predefined", cacheConfiguration.disableCachingNullValues()))
                .build();

        return cm;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(final LettuceConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }

}
