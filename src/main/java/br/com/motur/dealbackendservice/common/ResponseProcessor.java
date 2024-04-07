package br.com.motur.dealbackendservice.common;

import br.com.motur.dealbackendservice.core.model.EndpointConfigEntity;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Essa classe é responsável por processar a resposta de uma requisiçãoerfdeferd
 */
@Component
public final class ResponseProcessor {

    private final ObjectMapper mapper;

    @Autowired
    private ResponseProcessor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Processa a resposta de uma requisição e retorna um objeto do tipo especificado.
     *
     * @param jsonResponse A resposta da requisição
     * @param config      Configurações de mapeamento
     * @return O objeto de retorno
     */
    public Map<ResponseMapping.FieldMapping, Object> processAsHashMap(final Object jsonResponse, final ResponseMapping.Config config) {
        final EnumMap<ResponseMapping.FieldMapping, Object> resultMap = new EnumMap<>(ResponseMapping.FieldMapping.class);
        Object value = null;

        if (config.getOriginDatatype() == DataType.LIST) {
            value = processListDataType(jsonResponse, config);
        } else if (config.getOriginDatatype() == DataType.MAP) {
            value = processMapDataType(jsonResponse, config);
        } else if (config.getOriginDatatype() == DataType.JSON) {
            value = processJsonDataType(jsonResponse, config);
        }
        else if (config.getOriginDatatype() == DataType.STRING || config.getOriginDatatype() == DataType.INT
                || config.getOriginDatatype() == DataType.LONG || config.getOriginDatatype() == DataType.FLOAT
                || config.getOriginDatatype() == DataType.BOOLEAN || config.getOriginDatatype() == DataType.BIG_DECIMAL
                || config.getOriginDatatype() == DataType.DATE
                || config.getOriginDatatype() == DataType.LOCAL_DATETIME
                || config.getOriginDatatype() == DataType.CHAR || config.getOriginDatatype() == DataType.SHORT) {

            value = jsonResponse.toString();
        }

        if (config.getReturns() != null && ResponseMapping.FieldMapping.RETURNS.equals(config.getDestination()) && value != null) {
            value = processAsHashMap(value, config.getReturns());
        }

        resultMap.put(config.getDestination(), value);
        return resultMap;
    }

    private Object processListDataType(Object jsonResponse, ResponseMapping.Config config) {

        Object value = null;
        if (config.getOriginPath().startsWith("[") && config.getOriginPath().endsWith("]")) {
            Integer index = Integer.parseInt(config.getOriginPath().replaceAll("[\\[\\]]", ""));
            if (jsonResponse instanceof List<?>) {
                value = ((List)jsonResponse).get(index);
            }
            else if (jsonResponse instanceof Map<?,?>) {
                value = ((Map<Integer, Object>)jsonResponse).get(index);
            }
        } else {

            if (jsonResponse instanceof List<?>) {

                final List<Object> list = new ArrayList<>();

                if (!((List)jsonResponse).isEmpty()){

                    if (((List)jsonResponse).get(0) instanceof Map<?,?>) {

                        ((List)jsonResponse).forEach(item -> {
                            Map<Object,Object> map = (Map<Object, Object>) item;
                            list.add(map.get(config.getOriginPath()));
                        });
                    }

                    value = list;
                }
            }
        }

        return value;
    }

    private Object processMapDataType(Object jsonResponse, ResponseMapping.Config config) {

        Object value = null;
        if (config.getOriginPath().equals("#key")) { // Retorna as chaves
            value = ((Map<Object, Object>)jsonResponse).keySet();
        } else if (config.getOriginPath().equals("#value")) { // Retorna os valores
            value = ((Map<Object, Object>)jsonResponse).values();
        } else {
            value = ((Map<Object, Object>)jsonResponse).get(config.getOriginPath()); // Retorna o valor de uma chave específica
        }

        return value;
    }

    private Object processJsonDataType(Object jsonResponse, ResponseMapping.Config config) {
        return ((JsonNode)jsonResponse).get(config.getOriginPath());
    }

    /**
     * Processa a resposta de uma requisição e retorna um HashMap com os valores mapeados.
     *
     * @param jsonResponse A resposta da requisição
     * @param configs      Configurações de mapeamento
     * @return O objeto de retorno
     */
    public Map<ResponseMapping.FieldMapping, Object> getMappingValues(final Object jsonResponse, final List<ResponseMapping.Config> configs) {

        final EnumMap<ResponseMapping.FieldMapping, Object> resultMap = new EnumMap<>(ResponseMapping.FieldMapping.class);

        for (var item : configs) {

            Map<ResponseMapping.FieldMapping, Object> result = processAsHashMap(jsonResponse, item);

            for (ResponseMapping.FieldMapping value : ResponseMapping.FieldMapping.values()) {

                if (ResponseMapping.FieldMapping.RETURNS.equals(value)) {
                    continue;
                }

                Map<ResponseMapping.FieldMapping, Object> currentMap = result;
                while (currentMap.get(ResponseMapping.FieldMapping.RETURNS) != null) {
                    currentMap = (HashMap<ResponseMapping.FieldMapping, Object>) currentMap.get(ResponseMapping.FieldMapping.RETURNS);
                }

                if(currentMap.get(value) != null){
                    resultMap.put(value, currentMap.get(value));
                }
            }
        }

        return resultMap;
    }

    /**
     * Obter o valor de um campo de um HashMap aninhado
     * @param mapping Configuração do campos
     * @param origin Mapa aninhado de origem
     */
    public Object getValueFromNestedMap(final ResponseMapping mapping, Map<Object, Object> origin, Logger logger) {

        // Obtendo os valores mapeados
        final Map<ResponseMapping.FieldMapping, Object> fieldMappings = getMappingValues(origin, mapping.getFieldMappings());

        var externalIds = fieldMappings.get(ResponseMapping.FieldMapping.EXTERNAL_ID);
        var names = fieldMappings.get(ResponseMapping.FieldMapping.NAME);

        // Convertendo para lista se necessário
        if (externalIds != null && (externalIds  instanceof LinkedHashMap || externalIds instanceof SequencedCollection)) {
            externalIds = mapper.convertValue(externalIds, List.class);
        }

        // Convertendo para lista se necessário
        if (names != null && (names instanceof LinkedHashMap || names instanceof SequencedCollection)) {
            names = mapper.convertValue(names, List.class);
        }

        if (externalIds == null || names == null || ((List)externalIds).size() != ((List)names).size()) {
            logger.error("O External ID e o Name não foram encontrados ou não possuem o mesmo tamanho da lista. External ID:{} - Name: {}", externalIds, names);
        } else {
            // Merging the lists into a HashMap
            Map<Object, Object> map = new HashMap<>();
            for (int i = 0; i < ((List)externalIds).size(); i++) {
                map.put(((List)externalIds).get(i), ((List)names).get(i));
            }

            return map;
        }

        return new HashMap<>(); // Retornando um mapa vazio se não for possível obter os valores
    }

    /**
     * Obter o valor de um campo de um HashMap aninhado
     * @param mapping Configuração do campos
     * @param origin Mapa aninhado de origem
     */
    public String getStringFieldFromNestedMap(final ResponseMapping.FieldMapping fieldMapping, final ResponseMapping mapping, final Map<Object, Object> origin, final Logger logger) {

        // Obtendo os valores mapeados
        final Map<ResponseMapping.FieldMapping, Object> fieldMappings = getMappingValues(origin, mapping.getFieldMappings());

        var field = fieldMappings.get(fieldMapping);

        if (field == null) {
            logger.error("O campo {} não foi encontrado.", fieldMapping);
            return StringUtils.EMPTY;
        }

        return field.toString();
    }

    /**
     * Formata um campo JSON com os valores passados
     * @param endpointConfigEntity Configuração do endpoint
     * @param key Chave a ser substituída
     * @return value Valor a ser substituído
     */
    public void updateEndpointConfigFields(EndpointConfigEntity endpointConfigEntity, String key, String value) {
        // Update URL, headers, additionalParams, and payload using the methods similar to those we discussed earlier
        endpointConfigEntity.setUrl(endpointConfigEntity.getUrl() != null ? endpointConfigEntity.getUrl().replace("{" + key + "}", value) : null);
        if (endpointConfigEntity.getHeaders() != null) {
            endpointConfigEntity.setHeaders(formatJsonField(endpointConfigEntity.getHeaders(), Map.of(key, value)));
        }
        if (endpointConfigEntity.getAdditionalParams() != null) {
            endpointConfigEntity.setAdditionalParams(formatJsonField(endpointConfigEntity.getAdditionalParams(), Map.of(key, value)));
        }
        if (endpointConfigEntity.getPayload() != null) {
            endpointConfigEntity.setPayload(formatJsonField(endpointConfigEntity.getPayload(), Map.of(key, value)));
        }
    }

    /**
     * Formata um campo json com os campos informados
     * @param json Json a ser formatado
     * @param fields Campos a serem adicionados
     */
    public JsonNode formatJsonField(final JsonNode json, final Map<String,Object> fields) {

        String jsonString = json != null ? new String(json.toString().getBytes(StandardCharsets.UTF_8)) : StringUtils.EMPTY;

        for (var entry : fields.entrySet()) {
            jsonString = jsonString.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }

        try {
            return mapper.readValue(jsonString.getBytes(StandardCharsets.UTF_8), JsonNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica se o endpoint é parametrizado
     * @param endpointConfigEntity Configuração do endpoint
     */
    public boolean isParametrizedEndpoint(final EndpointConfigEntity endpointConfigEntity) {

        for (final ResponseMapping.FieldMapping field : ResponseMapping.FieldMapping.values()) {

            final String value = "{" + field + "}";

            if (endpointConfigEntity.getUrl() != null && endpointConfigEntity.getUrl().contains(value)) {
                return true;
            }

            Iterable<Map.Entry<String, JsonNode>> iterable = () -> endpointConfigEntity.getHeaders().fields();
            if (endpointConfigEntity.getHeaders() != null && !endpointConfigEntity.getHeaders().isNull() && StreamSupport.stream(iterable.spliterator(), false).anyMatch(f -> f.getValue().textValue().contains(value))){
                return true;
            }

            iterable = () -> endpointConfigEntity.getAdditionalParams().fields();
            if (endpointConfigEntity.getAdditionalParams() != null && !endpointConfigEntity.getAdditionalParams().isNull() && StreamSupport.stream(iterable.spliterator(), false).anyMatch(f -> f.getValue().textValue().contains(value))){
                return true;
            }

            iterable = () -> endpointConfigEntity.getPayload().fields();
            if (endpointConfigEntity.getPayload() != null && !endpointConfigEntity.getPayload().isNull() && StreamSupport.stream(iterable.spliterator(), false).anyMatch(f -> f.getValue().textValue().contains(value))){
                return true;
            }

        }
        return false;
    }

    private boolean isParametrizedField(final String field) {
        return field.startsWith("{") && field.endsWith("}");
    }
}

