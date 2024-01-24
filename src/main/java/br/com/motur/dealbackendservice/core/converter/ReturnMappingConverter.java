package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.core.model.common.ReturnMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Converter
public class ReturnMappingConverter implements AttributeConverter<ReturnMapping, String> {

    private final ObjectMapper objectMapper;

    @Autowired
    public ReturnMappingConverter(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(final ReturnMapping jsonNode) {
        try {
            return jsonNode != null ? objectMapper.writeValueAsString(jsonNode) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON writing error", e);
        }
    }

    @Override
    public ReturnMapping convertToEntityAttribute(final String json) {
        try {
            return json != null ? objectMapper.readValue(json, ReturnMapping.class) : null;
        } catch (Exception e) {
            throw new RuntimeException("JSON reading error", e);
        }
    }
}
