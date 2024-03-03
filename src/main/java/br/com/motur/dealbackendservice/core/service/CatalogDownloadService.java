package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.ResponseProcessor;
import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.motur.dealbackendservice.core.model.common.IntegrationFields.BRAND_ID;
import static br.com.motur.dealbackendservice.core.model.common.IntegrationFields.MODEL_ID;

/**
 * Serviço responsável por realizar o download dos dados do catalogo dos fornecedores
 */
@Service
public class CatalogDownloadService {

    private final ProviderRepository providerRepository;
    private final EndpointConfigRepository endpointConfigRepository;
    private final RequestRestService requestRestService;
    private final RequestSoapService requestSoapService;
    private final ProviderBrandsRepository providerBrandsRepository;
    private final ProviderModelsRepository providerModelsRepository;
    private final ProviderTrimsRepository providerTrimsRepository;
    private final BrandRepository brandRepository;

    private final ModelRepository modelRepository;

    private final TrimRepository trimRepository;

    private final ObjectMapper objectMapper;

    private final ModelMapper modelMapper;

    private final ResponseProcessor responseProcessor;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public CatalogDownloadService(ProviderRepository providerRepository, EndpointConfigRepository endpointConfigRepository,
                                  RequestRestService requestRestService, RequestSoapService requestSoapService,
                                  ProviderBrandsRepository providerBrandsRepository, ProviderModelsRepository providerModelsRepository,
                                  ProviderTrimsRepository providerTrimsRepository, BrandRepository brandRepository,
                                  ModelRepository modelRepository, TrimRepository trimRepository, ObjectMapper objectMapper, ModelMapper modelMapper, ResponseProcessor responseProcessor) {
        this.providerRepository = providerRepository;
        this.endpointConfigRepository = endpointConfigRepository;
        this.requestRestService = requestRestService;
        this.requestSoapService = requestSoapService;
        this.providerBrandsRepository = providerBrandsRepository;
        this.providerModelsRepository = providerModelsRepository;
        this.providerTrimsRepository = providerTrimsRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.trimRepository = trimRepository;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.responseProcessor = responseProcessor;
    }


    /**
     * Baixando catálogo de todos os fornecedores
     */
    public void downloadCatalogData() {

        logger.info("Baixando catálogo de todos os fornecedores");
        final List<ProviderEntity> providers = providerRepository.findAllAutoDownloadCatalog();
        for (ProviderEntity provider : providers) {

            logger.info("Baixando catálogo do fornecedor: {}",provider.getName());
            final List<EndpointConfig> authEndpoint = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, provider);

            downloadBrandsCatalog(provider, /*!authEndpoint.isEmpty() ? authEndpoint.get(0) :*/ null); // Baixando marcas
            downloadModelsCatalog(provider, /*!authEndpoint.isEmpty() ? authEndpoint.get(0) :*/ null); // Baixando modelos
            downloadTrimsCatalog(provider, /*!authEndpoint.isEmpty() ? authEndpoint.get(0) :*/ null); // Baixando versões

            logger.info("Catalogo do {} foi baixado", provider.getName());
        }
    }

    /**
     * Obter o valor de um campo de um HashMap aninhado
     * @param mapping Configuração do campos
     * @param origin Mapa aninhado de origem
     */
    private Object getValueFromNestedMap(final ResponseMapping mapping, Map<Object, Object> origin) {

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

    /**
     * Download das marcas do fornecedor
     * @param provider
     * @param authEndpoint Endpoint de autenticação
     */
    public void downloadBrandsCatalog(final ProviderEntity provider, final EndpointConfig authEndpoint) {
        final List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_BRANDS, provider);
        for (EndpointConfig endpointConfig : catalogEndpoints) {
            Map<Object, Object> results = requestRestService.getAsMap(provider, endpointConfig, authEndpoint);
            if (results != null && !results.isEmpty()) {
                processAndSaveCatalog(results, endpointConfig, provider, ProviderBrands.class, brandRepository.findAll(), null, providerBrandsRepository.findAllByProvider(provider), providerBrandsRepository);
            }
        }
    }

    /**
     * Download dos modelos do catalog do fornecedor
     * @param provider Provedor
     * @param authEndpoint Endpoint de autenticação
     */
    public void downloadModelsCatalog(final ProviderEntity provider, final EndpointConfig authEndpoint) {
        final List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_MODELS, provider);
        catalogEndpoints.forEach(endpointConfig -> processCatalogEndpointForModels(provider, endpointConfig, authEndpoint));
    }

    private void processCatalogEndpointForModels(final ProviderEntity provider, final EndpointConfig originalEndpointConfig, final EndpointConfig authEndpoint) {
        List<ProviderBrands> brands = providerBrandsRepository.findAllByProviderId(provider.getId());
        brands.forEach(brand -> updateAndProcessEndpointForBrand(provider, brand, cloneEndpointConfig(originalEndpointConfig), authEndpoint));
    }

    private void updateAndProcessEndpointForBrand(final ProviderEntity provider, final ProviderBrands brand, EndpointConfig endpointConfig, final EndpointConfig authEndpoint) {
        try {
            updateEndpointConfigForBrand(endpointConfig, brand);
            Map<Object, Object> results = (Map<Object, Object>) requestRestService.execute(provider, endpointConfig, authEndpoint);
            if (results != null && !results.isEmpty()) {
                processAndSaveCatalog(results, endpointConfig, provider, ProviderModels.class, modelRepository.findAllByBrand(brand.getBaseCatalog().getId()), brand, providerModelsRepository.findAllByParentProviderCatalog(brand), providerModelsRepository);
            }
        } catch (Exception e) {
            logger.error("Error processing models for brand: {} - {}", brand.getName(), e.getMessage(), e);
        }
    }

    private void updateEndpointConfigForBrand(EndpointConfig endpointConfig, ProviderBrands brand) {
        String brandId = brand.getExternalId();
        updateEndpointConfigFields(endpointConfig, BRAND_ID.getNormalizedValue(), brandId);
    }

    // Utility method to update fields in the EndpointConfig
    private void updateEndpointConfigFields(EndpointConfig endpointConfig, String key, String value) {
        // Update URL, headers, additionalParams, and payload using the methods similar to those we discussed earlier
        endpointConfig.setUrl(endpointConfig.getUrl().replace("{" + key + "}", value));
        if (endpointConfig.getHeaders() != null) {
            endpointConfig.setHeaders(formatJsonField(endpointConfig.getHeaders(), Map.of(key, value)));
        }
        if (endpointConfig.getAdditionalParams() != null) {
            endpointConfig.setAdditionalParams(formatJsonField(endpointConfig.getAdditionalParams(), Map.of(key, value)));
        }
        if (endpointConfig.getPayload() != null) {
            endpointConfig.setPayload(formatJsonField(endpointConfig.getPayload(), Map.of(key, value)));
        }
    }

    /**
     * Download the versões catalogo do fornecedor
     *
     * @param provider Provedor/Fornecedor
     * @param authEndpoint Endpoint de autenticação (opcional)
     */
    public void downloadTrimsCatalog(final ProviderEntity provider, final EndpointConfig authEndpoint) {
        final List<EndpointConfig> catalogEndpoints = fetchTrimCatalogEndpoints(provider);
        catalogEndpoints.forEach(endpointConfig -> processEachModel(provider, cloneEndpointConfig(endpointConfig), authEndpoint));
    }

    private List<EndpointConfig> fetchTrimCatalogEndpoints(ProviderEntity provider) {
        return endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_TRIMS, provider);
    }

    private EndpointConfig cloneEndpointConfig(EndpointConfig originalEndpointConfig) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(originalEndpointConfig), EndpointConfig.class);
        } catch (JsonProcessingException e) {
            logger.error("Error cloning endpoint config: " + e.getMessage(), e);
            return modelMapper.map(originalEndpointConfig, EndpointConfig.class);
        }
    }

    private void processEachModel(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig authEndpoint) {
        final List<ProviderModels> models = providerModelsRepository.findAllByProviderId(provider.getId());
        models.forEach(model -> processEachTrim(provider, model, updateEndpointConfigWithModelInfo(endpointConfig, model), authEndpoint));
    }

    private EndpointConfig updateEndpointConfigWithModelInfo(EndpointConfig endpointConfig, ProviderModels model) {
        Map<String, String> replacements = Map.of(
                MODEL_ID.getNormalizedValue(), model.getExternalId(),
                BRAND_ID.getNormalizedValue(), model.getParentProviderCatalog().getExternalId()
        );

        replaceEndpointConfigFields(endpointConfig, replacements);
        return endpointConfig;
    }

    private void replaceEndpointConfigFields(EndpointConfig endpointConfig, Map<String, String> replacements) {
        replacements.forEach((key, value) -> {
            replaceInUrl(endpointConfig, key, value);
            replaceInHeaders(endpointConfig, key, value);
            replaceInAdditionalParams(endpointConfig, key, value);
            replaceInPayload(endpointConfig, key, value);
        });
    }

    private void processEachTrim(ProviderEntity provider, ProviderModels model, EndpointConfig endpointConfig, EndpointConfig authEndpoint) {
        final Map<Object, Object> results = (Map<Object, Object>) requestRestService.execute(provider, endpointConfig, authEndpoint);
        if (results != null && !results.isEmpty()) {
            processAndSaveCatalog(results, endpointConfig, provider, ProviderTrims.class, trimRepository.findAllByModelId(model.getBaseModel().getId()), model, providerTrimsRepository.findAllByParentProviderCatalog(model), providerModelsRepository);
        }
    }

    private void replaceInUrl(EndpointConfig endpointConfig, String key, String value) {
        String updatedUrl = endpointConfig.getUrl().replace("{" + key + "}", value);
        endpointConfig.setUrl(updatedUrl);
    }

    private void replaceInHeaders(EndpointConfig endpointConfig, String key, String value) {
        if (endpointConfig.getHeaders() != null && !endpointConfig.getHeaders().isNull()) {
            Iterator<String> fieldNames = endpointConfig.getHeaders().fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                String fieldValue = endpointConfig.getHeaders().get(fieldName).textValue();
                if (fieldValue.contains("{" + key + "}")) {
                    ((ObjectNode) endpointConfig.getHeaders()).put(fieldName, fieldValue.replace("{" + key + "}", value));
                }
            }
        }
    }

    private void replaceInAdditionalParams(EndpointConfig endpointConfig, String key, String value) {
        JsonNode params = endpointConfig.getAdditionalParams();
        if (params != null && params.isObject()) {
            ObjectNode paramsObj = (ObjectNode) params;
            paramsObj.fieldNames().forEachRemaining(fieldName -> {
                String fieldValue = paramsObj.get(fieldName).asText();
                String updatedValue = fieldValue.replace("{" + key + "}", value);
                paramsObj.put(fieldName, updatedValue);
            });
        }
    }

    private void replaceInPayload(EndpointConfig endpointConfig, String key, String value) {
        JsonNode payload = endpointConfig.getPayload();
        if (payload != null && payload.isTextual()) {
            String updatedPayload = payload.asText().replace("{" + key + "}", value);
            endpointConfig.setPayload(new TextNode(updatedPayload));
        }
    }

    /**
     * Formata um campo json com os campos informados
     * @param json Json a ser formatado
     * @param fields Campos a serem adicionados
     */
    private JsonNode formatJsonField(final JsonNode json, final Map<String,Object> fields) {
        final Map<Object,Object> payload = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = json.fields();
        while (fieldsIterator.hasNext()) {
            final Map.Entry<String, JsonNode> field = fieldsIterator.next();
            payload.put(field.getKey(), objectMapper.convertValue(field.getValue(), Object.class));
        }

        fields.forEach((key, value) -> {
            payload.put(key, value);
        });

        return objectMapper.valueToTree(payload);
    }

    /**
     * Download the trims catalog from the fornecedor
     *
     * @param data                Dados recebidos do fornecedor
     * @param endpointConfig      Endpoint de autenticação
     * @param provider            Provedor
     * @param providerCatalogClassType Tipo da classe do item do catalogo do fornecedor
     * @param providerCatalogList Lista de dos itens do catalogo do fornecedor
     * @param parentProviderCatalog Item pai do catalogo do fornecedor
     * @param providerCatalogRepository Repository do item do catalogo do fornecedor
     */
    private void processAndSaveCatalog(final Map<Object, Object> data,
                                       final EndpointConfig endpointConfig,
                                       final ProviderEntity provider,
                                       final Class<? extends ProviderCatalogEntity> providerCatalogClassType,
                                       final List<? extends CatalogEntity> catalogEntities,
                                       final ProviderCatalogEntity parentProviderCatalog,
                                       final List<? extends ProviderCatalogEntity> providerCatalogList,
                                       final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        logger.info("Processando e salvando dados do catalogo do fornecedor: {}",provider.getName());

        //Extrai a lista raiz de itens de catalogo do retorno do fornecedor
        final Object list = getValueFromNestedMap(endpointConfig.getResponseMapping(), data);

        if (list instanceof List && !((List)list).isEmpty() && ((List)list).get(0) instanceof Map){

            final List brandList = (List) list;
            final List<Map<Object, Object>> listMap = (List<Map<Object, Object>>) list;

            final Map<Object, Object> mapList = listMap.stream()
                    .flatMap(m -> m.entrySet().stream())
                    .collect(Collectors.toMap(
                            entry -> entry.getKey(),
                            entry -> entry.getValue(),
                            (v1, v2) -> v1
                    ));

            brandList.forEach(brandMap -> processReturnMap(endpointConfig,
                    mapList,
                    providerCatalogClassType,
                    catalogEntities,
                    providerCatalogList,
                    parentProviderCatalog,
                    provider,
                    providerCatalogRepository));

        } else if (list instanceof Map) {

            processReturnMap(endpointConfig, (Map<Object, Object>) list, providerCatalogClassType, catalogEntities, providerCatalogList, parentProviderCatalog, provider, providerCatalogRepository);

        } else if (list instanceof JsonNode) {

            JsonNode jsonNode = null;

            try {
                jsonNode = (JsonNode) list;
            }
            catch (ClassCastException e){
                try {
                    jsonNode = objectMapper.readValue(list.toString(), JsonNode.class);
                } catch (JsonProcessingException ex) {
                    try {
                        jsonNode = objectMapper.readValue(objectMapper.writeValueAsString(list), JsonNode.class);
                    } catch (JsonProcessingException exc) {
                        logger.error("Erro ao processar o retorno do fornecedor: " + exc.getMessage(), exc);
                    }
                }
            }

            if (jsonNode != null){

                final Map<Object, Object> mapList = ((Map<Object, Object>)objectMapper.convertValue(jsonNode, Map.class)).entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue(),
                                (v1, v2) -> v1
                        ));

                processReturnMap(endpointConfig, mapList, providerCatalogClassType, catalogEntities, providerCatalogList, parentProviderCatalog, provider, providerCatalogRepository);
            }

        }

        logger.info("Dados do catalogo do fornecedor " + provider.getName() + " foram processados e salvos");

    }


    /**
     * Processa o mapa de retorno do fornecedor
     *
     * @param endpointConfig Endpoint de autenticação
     * @param brandMap Mapa de retorno do fornecedor
     * @param providerCatalogClassType Tipo da classe do item do catalogo do fornecedor (Brand, Model, Trim)
     * @param catalogEntityList Lista de dos itens do catalogo do fornecedor
     * @param providerCatalogList Lista de dos itens do catalogo do fornecedor
     * @param parentProviderCatalog Item pai do catalogo do fornecedor
     * @param provider Fornecedor
     * @param providerCatalogRepository Repositorio do item do catalogo do fornecedor
     */
    private void processReturnMap(final EndpointConfig endpointConfig,
                                  final Map<Object, Object> brandMap,
                                  final Class<? extends ProviderCatalogEntity> providerCatalogClassType,
                                  final List<? extends CatalogEntity> catalogEntityList,
                                  final List<? extends ProviderCatalogEntity> providerCatalogList,
                                  final ProviderCatalogEntity parentProviderCatalog,
                                  final ProviderEntity provider,
                                  final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        final List<ProviderCatalogEntity> providerCatalogsToSave = new ArrayList<>();

        brandMap.forEach((key, value) -> {

            final String externalId = key.toString();
            final String name = value.toString();

            final CatalogEntity entity = endpointConfig.getCategory().getFinderInstance().find(catalogEntityList, name);
            if (entity != null) {
                final ProviderCatalogEntity providerCatalog = findOrCreateProviderCatalog(externalId, (List<ProviderCatalogEntity>) providerCatalogList, providerCatalogClassType);
                providerCatalog.setName(name);
                providerCatalog.setExternalId(externalId);
                providerCatalog.setBaseCatalog(entity);
                providerCatalog.setParentProviderCatalog(parentProviderCatalog);
                providerCatalog.setProvider(provider);
                providerCatalogsToSave.add(providerCatalog);
            }
        });

        ((JpaRepository)providerCatalogRepository).saveAll(providerCatalogsToSave);
    }

    private ProviderCatalogEntity findOrCreateProviderCatalog(final String externalId, final List<ProviderCatalogEntity> providerCatalogs, final Class<? extends ProviderCatalogEntity> classType) {

        try {

            final Constructor<?> constructor = classType.getDeclaredConstructor();
            return providerCatalogs.stream().filter(p ->(p).getExternalId().equals(externalId)).findFirst().orElse((ProviderCatalogEntity) constructor.newInstance());
        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException | NoSuchMethodException e) {

        }

        return null;
    }

}
