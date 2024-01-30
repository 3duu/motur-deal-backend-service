package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

@Converter
public class ReturnMappingConverter implements AttributeConverter<ResponseMapping, String> {

    private final ObjectMapper objectMapper;

    @Autowired
    public ReturnMappingConverter(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(final ResponseMapping jsonNode) {
        try {
            return jsonNode != null ? objectMapper.writeValueAsString(jsonNode) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON writing error", e);
        }
    }

    @Override
    public ResponseMapping convertToEntityAttribute(final String json) {
        try {
            return json != null ? objectMapper.readValue(json, ResponseMapping.class) : null;
        } catch (Exception e) {
            throw new RuntimeException("JSON reading error", e);
        }
    }
}
