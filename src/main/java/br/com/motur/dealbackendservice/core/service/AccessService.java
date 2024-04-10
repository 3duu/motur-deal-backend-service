package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.ResponseProcessor;
import br.com.motur.dealbackendservice.core.model.common.ApiType;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public abstract class AccessService {

    protected final ApplicationContext applicationContext;
    protected final ResponseProcessor responseProcessor;
    protected final ObjectMapper objectMapper;

    protected final ModelMapper modelMapper;
    protected final Logger logger;

    @Autowired
    public AccessService(final ApplicationContext applicationContext, ResponseProcessor responseProcessor, ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.logger = LoggerFactory.getLogger(getClass());
        this.applicationContext = applicationContext;
        this.responseProcessor = responseProcessor;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    /**
     * Obter o serviço de requisição
     * @param apiType Tipo da API
     * @return Serviço de requisição
     */
    protected RequestService getRequestService(final ApiType apiType) {
        return applicationContext.getBean(apiType.getRequestService());
    }

    /**
     * Obter o id e nome de um campo de um HashMap aninhado
     * @param mapping Configuração do campos
     * @param origin Mapa aninhado de origem
     */
    protected List<Map<String, Object>> getIdAndNameFromNestedMap(final ResponseMapping mapping, Map<Object, Object> origin) {

        // Obtendo os valores mapeados
        final EnumMap<ResponseMapping.FieldMapping, Object> parsedResponse = responseProcessor.parseMappingValues(origin, mapping.getFieldMappings());

        // Output list
        final List<Map<String, Object>> outputList = new ArrayList<>();
        final AtomicReference<Type> type = new AtomicReference<>(null);

        for (Map.Entry<ResponseMapping.FieldMapping, Object> entry : parsedResponse.entrySet()) {

                if (entry.getValue() instanceof List && !((List)entry.getValue()).isEmpty()) {

                    final List list = (List) entry.getValue();
                    type.set(list.get(0).getClass());
                }
                else if (entry.getValue() instanceof Map && !((Map)entry.getValue()).isEmpty()) {

                    final Map map = (Map) entry.getValue();
                    type.set(map.get(0).getClass());
                }
                else if (entry.getValue() instanceof JsonNode) {
                    type.set(JsonNode.class);
                }
                else {
                    type.set(entry.getValue().getClass());
                }

                break;
        }

        // Assuming all lists in the map are of the same size
        if (type == null || type.get() == null){
            logger.error("Não foi possível obter o tipo de dado do campo. Tipo: {}", type);
            return new ArrayList<>();
        }

        if (type.get() == LinkedHashMap.class || type.get() == HashMap.class || type.get() == Map.class) {
            //return getIdAndNameFromNestedMap(origin);
        }
        else if (type.get() == JsonNode.class) {
            //return getIdAndNameFromNestedJsonNode(origin);
        }
        else if (type.get() == List.class || type.get() == ArrayList.class || type.get() == LinkedList.class || type.get() == Set.class || type.get() == Collection.class) {
            //return getIdAndNameFromNestedList(origin);
        }
        else {
            final int listSize = ((List)parsedResponse.get(ResponseMapping.FieldMapping.EXTERNAL_ID)).size();
            for (int i = 0; i < listSize; i++) {
                Map<String, Object> tempMap = new HashMap<>();
                for (Map.Entry<ResponseMapping.FieldMapping, Object> entry : parsedResponse.entrySet()) {
                    tempMap.put(entry.getKey().getValue(), ((List)entry.getValue()).get(i));
                }
                outputList.add(tempMap);
            }
        }

        return outputList;
    }
}
