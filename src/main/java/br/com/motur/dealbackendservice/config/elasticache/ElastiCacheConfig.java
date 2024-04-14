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
import java.util.HashMap;
import java.util.Map;

import static io.lettuce.core.ReadFrom.MASTER;

@Configuration
@EnableCaching
public class ElastiCacheConfig {

    private final String host;
    private final int port;

    public ElastiCacheConfig(@Value("${spring.data.redis.host}") String host, @Value ("${spring.data.redis.port}") int port) {
        this.host = host;
        this.port = port;
    }

    final RedisValueSerializer serializer = new RedisValueSerializer();


    /*@Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager cm = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig())
                .withInitialCacheConfigurations(singletonMap("predefined", defaultCacheConfig().disableCachingNullValues()))
                .transactionAware()
                .build()
        return RedisCacheManager.create(connectionFactory);
    }*/

    /*@Bean
    public RedisCacheConfiguration cacheConfiguration() {

        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return cacheConfiguration;
    }*/

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("COLORS",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(4)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))).enableStatistics()
                .withCacheConfiguration("PROVIDER_CATALOG",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))).enableStatistics()
                .withCacheConfiguration("FIND_BY_CATHEGORY",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(48)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))).enableStatistics();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        final LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(MASTER)
                .build();

        final RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(host, port);
        serverConfig.setDatabase(0);

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    public RedisCacheManager cacheManager(final RedisConnectionFactory connectionFactory/*, final RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer*/) {

        final RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        RedisCacheManager cm = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration)
                //.withInitialCacheConfigurations(singletonMap("predefined", cacheConfiguration.disableCachingNullValues()))
                //.transactionAware()
                .withInitialCacheConfigurations(Map.of("predefined", cacheConfiguration.disableCachingNullValues()))
                .build();

        return cm;
    }

    private Map<String, RedisCacheConfiguration> singletonMap(String predefined, RedisCacheConfiguration redisCacheConfiguration) {
        Map<String, RedisCacheConfiguration> map = new HashMap<>();
        map.put(predefined, redisCacheConfiguration);
        return map;
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
