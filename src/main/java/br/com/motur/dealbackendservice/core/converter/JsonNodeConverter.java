package br.com.motur.dealbackendservice.core.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


import java.io.IOException;

/**
 * Converter para mapear um campo JSON em uma entidade JPA.
 * Notas:
 * Segurança: Garanta que as informações sensíveis armazenadas nesta tabela sejam protegidas adequadamente.
 * Flexibilidade: O uso de um campo JSON permite flexibilidade para armazenar diferentes tipos de dados de autenticação.
 * JSON e JPA: O suporte ao JSON em JPA pode variar dependendo da implementação do provedor JPA e do banco de dados usado. Certifique-se de que sua configuração suporta o mapeamento JSON adequadamente.
 */
@Converter
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON writing error", e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("JSON reading error", e);
        }
    }
}
