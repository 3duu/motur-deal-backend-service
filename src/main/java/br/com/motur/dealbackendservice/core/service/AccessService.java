package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.ResponseProcessor;
import br.com.motur.dealbackendservice.core.model.common.ApiType;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

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
     * Obter o valor de um campo de um HashMap aninhado
     * @param mapping Configuração do campos
     * @param origin Mapa aninhado de origem
     */
    protected Object getValueFromNestedMap(final ResponseMapping mapping, Map<Object, Object> origin) {

        // Obtendo os valores mapeados
        final Map<ResponseMapping.FieldMapping, Object> fieldMappings = responseProcessor.getMappingValues(origin, mapping.getFieldMappings());

        var externalIds = fieldMappings.get(ResponseMapping.FieldMapping.EXTERNAL_ID);
        var names = fieldMappings.get(ResponseMapping.FieldMapping.NAME);

        // Convertendo para lista se necessário
        if (externalIds != null && (externalIds  instanceof LinkedHashMap || externalIds instanceof SequencedCollection)) {
            externalIds = objectMapper.convertValue(externalIds, List.class);
        }

        // Convertendo para lista se necessário
        if (names != null && (names instanceof LinkedHashMap || names instanceof SequencedCollection)) {
            names = objectMapper.convertValue(names, List.class);
        }

        if (externalIds == null || names == null || ((List)externalIds).size() != ((List)names).size()) {
            logger.error("O External ID e o Name não foram encontrados ou não possuem o mesmo tamanho. External ID:{} - Name: {}", externalIds, names);
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
}
