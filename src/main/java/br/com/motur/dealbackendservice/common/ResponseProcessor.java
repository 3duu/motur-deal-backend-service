package br.com.motur.dealbackendservice.common;

import br.com.motur.dealbackendservice.core.model.common.DataType;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Essa classe é responsável por processar a resposta de uma requisiçãoerfdeferd
 */
@Component
public class ResponseProcessor {

    private final ObjectMapper mapper;

    @Autowired
    private ResponseProcessor(ObjectMapper mapper) {
        // Construtor privado para evitar instanciação
        this.mapper = mapper;
    }

    public Object processResponse(String jsonResponse, ResponseMapping mapping) {

        try {
            JsonNode rootNode = mapper.readTree(jsonResponse);
            JsonNode dataNode = rootNode.path(mapping.getFieldMappings().get(0).getOriginPath());

            if (dataNode.isObject()) {
                return processAsObject(dataNode, mapping.getFieldMappings().get(0));
            } else if (dataNode.isArray()) {
                return processAsList(dataNode, mapping.getFieldMappings().get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Processa a resposta de uma requisição e retorna um objeto do tipo especificado.
     *
     * @param jsonResponse A resposta da requisição
     * @param configs      A lista de configurações de mapeamento
     * @return O objeto de retorno
     */
    public JsonNode processAsHashMap(Map<Object, Object> jsonResponse, List<ResponseMapping.Config> configs) {

        final ObjectNode resultNode = mapper.createObjectNode();
        final Map<ResponseMapping.FieldMapping, Object> resultMap = new HashMap();

        if (configs.isEmpty())
            return resultNode;

        for (var config : configs){

            final Object returns = resultMap.keySet().stream().filter(c -> ResponseMapping.FieldMapping.RETURNS.equals(c)).findAny().isPresent() ?  resultMap.get(ResponseMapping.FieldMapping.RETURNS) : null;
            if (returns != null){
                // Processamento recursivo
                JsonNode subNode = processAsHashMap((Map<Object, Object>) returns, List.of(config.getReturns()));
                resultNode.set(config.getDestination().name(), subNode);
            }
            else {

                // Verifica se o originPath é um identificador especial como "#key" ou "#value"
                if (config.getOriginPath().startsWith("#")) {
                    for (Map.Entry<Object, Object> entry : jsonResponse.entrySet()) {
                        if (config.getOriginPath().equals("#key")) {
                            resultMap.put(config.getDestination(), entry.getKey());
                            resultNode.put(config.getDestination().name(), String.valueOf(entry.getKey()));
                        } else if (config.getOriginPath().equals("#value")) {
                            resultMap.put(config.getDestination(), entry.getValue());
                            resultNode.put(config.getDestination().name(), String.valueOf(entry.getValue()));
                        }
                    }
                } else if (!config.getOriginPath().contains("[")) {
                    // Tratamento de uma chave simples de HashMap
                    String key = config.getOriginPath();
                    Object value = jsonResponse.get(key);
                    if (value != null) {
                        resultNode.put(config.getDestination().name(), String.valueOf(value));
                        resultMap.put(config.getDestination(), value);
                    }
                }
                else if (config.getOriginPath().startsWith("[")) {
                    // Lógica para processar índices específicos, ex: "[0]"
                    int index = Integer.parseInt(config.getOriginPath().replaceAll("[\\[\\]]", ""));
                    Object itemNode = jsonResponse.get(index);
                    resultMap.put(config.getDestination(), itemNode);
                    resultNode.put(config.getDestination().name(), itemNode.toString());
                }
            }
        }

        return resultNode;
    }

    private JsonNode processAsJsonObject(JsonNode dataNode, ResponseMapping.Config config) {

        ObjectNode resultNode = mapper.createObjectNode();

        if (config.getOriginPath().startsWith("#")) {
            // Processamento especial para "#key" e "#value"
            dataNode.fields().forEachRemaining(entry -> {
                if (config.getOriginPath().equals("#key")) {
                    resultNode.put(config.getDestination().name(), entry.getKey());
                } else if (config.getOriginPath().equals("#value")) {
                    resultNode.put(config.getDestination().name(), entry.getValue().asText()); // Assumindo que o valor é um texto
                }
            });
        } else if (!config.getOriginPath().contains("[")) {
            // Tratamento de uma chave simples de HashMap
            String key = config.getOriginPath();
            JsonNode valueNode = dataNode.path(key);
            if (!valueNode.isMissingNode()) {
                resultNode.put(config.getDestination().name(), valueNode.asText()); // Assumindo que o valor é um texto
            }
        }
        return resultNode;
    }

    private static MyEntity processAsObject(JsonNode dataNode, ResponseMapping.Config config) {
        MyEntity entity = new MyEntity();
        dataNode.fields().forEachRemaining(entry -> {
            if (config.getOriginPath().equals("#key")) {
                entity.setName(entry.getKey());
            } else if (config.getOriginPath().equals("#value")) {
                entity.setId(entry.getValue().asLong());
            }
            else if (!config.getOriginPath().contains("[")) {
                // Tratamento de uma chave simples de HashMap
                String key = config.getOriginPath();
                JsonNode valueNode = dataNode.path(key);
                if (!valueNode.isMissingNode()) {
                    mapToEntity(entity, valueNode, config.getDestination());
                }
            }
            // Adicione mais lógica conforme necessário
        });
        return entity;
    }

    private static void mapToEntity(MyEntity entity, JsonNode valueNode, ResponseMapping.FieldMapping destination) {
        switch (destination) {
            case NAME:
                entity.setName(valueNode.asText());
                break;
            case EXTERNAL_ID:
                entity.setId(valueNode.asLong());
                break;
            // Adicione casos para outros mapeamentos conforme necessário
        }
    }

    private static List<MyEntity> processAsList(JsonNode dataNode, ResponseMapping.Config config) {
        List<MyEntity> entities = new ArrayList<>();
        if (config.getOriginPath().startsWith("[")) {
            // Lógica para processar índices específicos, ex: "[0]"
            int index = Integer.parseInt(config.getOriginPath().replaceAll("[\\[\\]]", ""));
            JsonNode itemNode = dataNode.get(index);
            entities.add(processAsObject(itemNode, config));
        } else {
            dataNode.forEach(node -> entities.add(processAsObject(node, config)));
        }
        return entities;
    }

    @Data
    public static class MyEntity {
        private Long id;
        private String name;
        // Getters e Setters
    }

    // Suponha que ResponseMapping e suas classes internas estejam definidas conforme fornecido
}

