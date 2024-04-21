package br.com.motur.dealbackendservice.config.elasticache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;


/**
 * Classe de serialização de valores para o Redis.
 */
public class RedisValueSerializer extends GenericJackson2JsonRedisSerializer {

    static final byte[] EMPTY_ARRAY = new byte[0];

    public RedisValueSerializer(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    @Nullable
    public <T> T deserialize(@Nullable byte[] source, Class<T> type) throws SerializationException {
        Assert.notNull(type, "Deserialization type must not be null! Please provide Object.class to make use of Jackson2 default typing.");
        if (isEmpty(source)) {
            return null;
        } else {
            try {
                var object = getObjectMapper().readValue(source, type);
                if (object.getClass().equals(LinkedHashMap.class)){
                    return getObjectMapper().readValue(getObjectMapper().writeValueAsString(object), type);
                }
            } catch (Exception var4) {
                throw new SerializationException("Could not read JSON: " + var4.getMessage(), var4);
            }
        }
        return super.deserialize(source, type);
    }

    static boolean isEmpty(@Nullable byte[] data) {
        return data == null || data.length == 0;
    }

}


