package br.com.motur.dealbackendservice.config.elasticache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;


/**
 * Classe de serialização de valores para o Redis.
 */
public class RedisValueSerializer extends GenericJackson2JsonRedisSerializer {

    static final byte[] EMPTY_ARRAY = new byte[0];

    public static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public byte[] serialize(@Nullable Object source) throws SerializationException {
        if (source == null) {
            return EMPTY_ARRAY;
        } else {
            try {
                if (source.getClass().equals(ResponseEntity.class)){

                   Object retorno = ((ResponseEntity<?>) source).getBody();
                    if(retorno == null){
                        return EMPTY_ARRAY;
                    }
                    var entityMap = mapper.readValue(this.mapper.writeValueAsString(source), LinkedHashMap.class);
                    entityMap.put("bodyType", ((ResponseEntity<?>) source).getBody().getClass().getName());
                    return this.mapper.writeValueAsBytes(entityMap);
                }
                return super.serialize(source);
            } catch (JsonProcessingException var3) {
                throw new SerializationException("Could not write JSON: " + var3.getMessage(), var3);
            }
        }
    }

    @Override
    public Object deserialize(@Nullable byte[] source) throws SerializationException {
        return this.deserialize(source, Object.class);
    }

    @Override
    @Nullable
    public <T> T deserialize(@Nullable byte[] source, Class<T> type) throws SerializationException {
        Assert.notNull(type, "Deserialization type must not be null! Please provide Object.class to make use of Jackson2 default typing.");
        if (isEmpty(source)) {
            return null;
        } else {
            try {
                var object = this.mapper.readValue(source, type);
                if (object.getClass().equals(LinkedHashMap.class)){
                    var map = (LinkedHashMap) object;
                    if (map.containsKey("body") && map.containsKey("statusCode") && map.containsKey("headers") && map.containsKey("bodyType")){
                        if (map.get("bodyType") != null && map.get("body") != null){
                            Class<?> cls = Class.forName(map.get("bodyType").toString());
                            var responseEntity = new ResponseEntity( mapper.readValue(this.mapper.writeValueAsString(map.get("body")), cls) ,HttpStatus.valueOf(map.get("statusCode").toString()));
                            return (T) responseEntity;
                        }
                    }
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


