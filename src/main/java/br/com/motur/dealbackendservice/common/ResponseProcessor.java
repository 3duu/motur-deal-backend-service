package br.com.motur.dealbackendservice.common;

import br.com.motur.dealbackendservice.core.model.common.DataType;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    /**
     * Processa a resposta de uma requisição e retorna um objeto do tipo especificado.
     *
     * @param jsonResponse A resposta da requisição
     * @param config      Configurações de mapeamento
     * @return O objeto de retorno
     */
    public Map<ResponseMapping.FieldMapping, Object> processAsHashMap(final Object jsonResponse, final ResponseMapping.Config config) {
        final Map<ResponseMapping.FieldMapping, Object> resultMap = new HashMap<>();

        Object value = null;
        if (config.getOriginDatatype() == DataType.LIST) {
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



        } else if (config.getOriginDatatype() == DataType.MAP) {
            if (config.getOriginPath().equals("#key")) {
                value = ((Map<Object, Object>)jsonResponse).keySet();
            } else if (config.getOriginPath().equals("#value")) {
                value = ((Map<Object, Object>)jsonResponse).values();
            } else {
                value = ((Map<Object, Object>)jsonResponse).get(config.getOriginPath());
            }
        }
        else if (config.getOriginDatatype() == DataType.JSON) {

            value = ((JsonNode)jsonResponse).get(config.getOriginPath());
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


    /**
     * Processa a resposta de uma requisição e retorna um HashMap com os valores mapeados.
     *
     * @param jsonResponse A resposta da requisição
     * @param configs      Configurações de mapeamento
     * @return O objeto de retorno
     */
    public Map<ResponseMapping.FieldMapping, Object> getMappingValues(final Object jsonResponse, final List<ResponseMapping.Config> configs) {

        final Map<ResponseMapping.FieldMapping, Object> resultMap = new HashMap<>();

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


}

