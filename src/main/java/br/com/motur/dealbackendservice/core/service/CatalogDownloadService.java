package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.ResponseProcessor;
import br.com.motur.dealbackendservice.config.service.CacheService;
import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.CacheNames;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import br.com.motur.dealbackendservice.core.model.common.IntegrationFields;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
public class CatalogDownloadService extends AccessService {


    private final ProviderRepository providerRepository;
    private final EndpointConfigRepository endpointConfigRepository;
    private final ProviderBrandsRepository providerBrandsRepository;
    private final ProviderModelsRepository providerModelsRepository;
    private final ProviderTrimsRepository providerTrimsRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final TrimRepository trimRepository;
    private final CacheService cacheService;


    @Autowired
    public CatalogDownloadService(final ApplicationContext applicationContext, final ProviderRepository providerRepository, final EndpointConfigRepository endpointConfigRepository,
                                  final ProviderBrandsRepository providerBrandsRepository, final ProviderModelsRepository providerModelsRepository,
                                  final ProviderTrimsRepository providerTrimsRepository, final BrandRepository brandRepository,
                                  final ModelRepository modelRepository, final TrimRepository trimRepository, final ObjectMapper objectMapper, final ModelMapper modelMapper, final ResponseProcessor responseProcessor, final CacheService cacheService) {
        super(applicationContext, responseProcessor, objectMapper, modelMapper);
        this.providerRepository = providerRepository;
        this.endpointConfigRepository = endpointConfigRepository;
        this.providerBrandsRepository = providerBrandsRepository;
        this.providerModelsRepository = providerModelsRepository;
        this.providerTrimsRepository = providerTrimsRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.trimRepository = trimRepository;
        this.cacheService = cacheService;
    }


    /**
     * Baixando catálogo de todos os fornecedores
     */
    public void downloadCatalogData() {

        logger.info("Baixando catálogo de todos os fornecedores");
        final List<ProviderEntity> providers = providerRepository.findAllAutoDownloadCatalog();
        for (ProviderEntity provider : providers) {

            logger.info("Baixando catálogo do fornecedor: {}",provider.getName());

            //downloadBrandsCatalog(provider); // Baixando marcas
            //downloadModelsCatalog(provider); // Baixando modelos
            downloadTrimsCatalog(provider); // Baixando versões

            logger.info("Catalogo do {} foi baixado", provider.getName());
        }
    }



    /**
     * Download das marcas do fornecedor
     * @param provider
     */
    public void downloadBrandsCatalog(final ProviderEntity provider) {
        final List<EndpointConfigEntity> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_BRANDS, provider);
        for (EndpointConfigEntity endpointConfigEntity : catalogEndpoints) {
            Map<Object, Object> results = getRequestService(provider.getApiType()).getAsMap(provider, endpointConfigEntity);
            if (results != null && !results.isEmpty()) {
                processAndSaveCatalog(results,
                        endpointConfigEntity,
                        provider,
                        ProviderBrandsEntity.class,
                        brandRepository.findAll(),
                        null,
                        providerBrandsRepository.findAllByProvider(provider),
                        providerBrandsRepository,
                        endpointConfigEntity.getCategory());
            }
        }
    }

    /**
     * Download dos modelos do catalog do fornecedor
     * @param provider Provedor
     */
    public void downloadModelsCatalog(final ProviderEntity provider) {
        final List<EndpointConfigEntity> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_MODELS, provider);
        catalogEndpoints.forEach(endpointConfig -> processCatalogEndpointForModels(provider, endpointConfig));
    }

    private void processCatalogEndpointForModels(final ProviderEntity provider, final EndpointConfigEntity originalEndpointConfigEntity) {

        final boolean isParametrized = responseProcessor.isParametrizedEndpoint(originalEndpointConfigEntity); // Verifica se o endpoint é parametrizado, caso seja, será feita várias chamadas e o valor do parâmetro é substituído em cada uma delas
        final List<ProviderBrandsEntity> brands = providerBrandsRepository.findAllByProviderId(provider.getId());
        EndpointConfigEntity cloneEndpointConfig = originalEndpointConfigEntity; // Clona o endpoint de configuração para não alterar o original

        Map<Object, Object> results = null;
        if (!isParametrized) {
            try {
                results = (Map<Object, Object>) getRequestService(provider.getApiType()).execute(provider, cloneEndpointConfig);
            } catch (Exception e) {
                logger.error("Erro ao processar modelos para o fornecedor: {} - {}", provider.getName(), e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        else {
            cloneEndpointConfig = cloneEndpointConfig(originalEndpointConfigEntity);
        }

        for (final ProviderBrandsEntity brand : brands) {

            if (isParametrized) {

                try {
                    bindExternalIdToEndpointConfig(cloneEndpointConfig, brand, BRAND_ID);
                    results = (Map<Object, Object>) getRequestService(provider.getApiType()).execute(provider, cloneEndpointConfig);
                } catch (Exception e) {
                    logger.error("Erro ao processar modelos para o fornecedor: {} - brand {}", provider.getName(), brand.getName(), e);
                    throw new RuntimeException(e);
                }
            }

            updateAndProcessEndpointForBrand(provider, brand, cloneEndpointConfig, results);
        }
    }

    /**
     * Atualiza e processa o endpoint com a marca para publicar modelos
     * @param provider Provedor
     * @param brand Marca
     * @param endpointConfigEntity Endpoint de configuração
     */
    private void updateAndProcessEndpointForBrand(final ProviderEntity provider, final ProviderBrandsEntity brand, final EndpointConfigEntity endpointConfigEntity, final Map<Object, Object> results) {
        try {

            if (results != null && !results.isEmpty()) {
                final List<ModelEntity> models = brand != null ? modelRepository.findAllByBrand(brand.getBaseCatalog().getId()) : modelRepository.findAll();
                processAndSaveCatalog(results,
                        endpointConfigEntity,
                        provider,
                        ProviderModelsEntity.class,
                        models,
                        brand,
                        providerModelsRepository.findAllByParentProviderCatalog(brand),
                        providerModelsRepository,
                        endpointConfigEntity.getCategory());
            }
        } catch (Exception e) {
            logger.error("Erro ao processar e salvar os modelos do fornecedor: {}", provider.getName(), e);
        }
    }


    /**
     * Atualiza o endpoint de configuração com o ID externo do catalogo
     * @param endpointConfigEntity Endpoint de configuração
     * @param catalogEntity Entidade do catalogo
     * @param field Campo
     */
    private void bindExternalIdToEndpointConfig(final EndpointConfigEntity endpointConfigEntity, ProviderCatalogEntity catalogEntity, IntegrationFields field) {
        if (catalogEntity == null) {
            return;
        }
        final String brandId = catalogEntity.getExternalId();
        responseProcessor.updateEndpointConfigFields(endpointConfigEntity, field.getNormalizedValue(), brandId);
    }

    /**
     * Download the versões catalogo do fornecedor
     *
     * @param provider Provedor/Fornecedor
     */
    public void downloadTrimsCatalog(final ProviderEntity provider) {
        final List<EndpointConfigEntity> catalogEndpoints = fetchTrimCatalogEndpoints(provider);
        catalogEndpoints.forEach(endpointConfig -> processEachModel(provider, cloneEndpointConfig(endpointConfig)));
    }

    private List<EndpointConfigEntity> fetchTrimCatalogEndpoints(ProviderEntity provider) {
        return endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_TRIMS, provider);
    }

    /**
     * Clona o endpoint de configuração
     *
     * @param originalEndpointConfigEntity Endpoint de configuração original
     * @return Endpoint de configuração clonado
     */
    private EndpointConfigEntity cloneEndpointConfig(EndpointConfigEntity originalEndpointConfigEntity) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(originalEndpointConfigEntity), EndpointConfigEntity.class);
        } catch (JsonProcessingException e) {
            logger.error("Error cloning endpoint config: {}",e.getMessage(), e);
            return modelMapper.map(originalEndpointConfigEntity, EndpointConfigEntity.class);
        }
    }

    private void processEachModel(final ProviderEntity provider, EndpointConfigEntity originalEndpointConfigEntity) {

        final boolean isParametrized = responseProcessor.isParametrizedEndpoint(originalEndpointConfigEntity); // Verifica se o endpoint é parametrizado, caso seja, será feita várias chamadas e o valor do parâmetro é substituído em cada uma delas
        final List<ProviderModelsEntity> models = providerModelsRepository.findAllByProviderId(provider.getId());
        EndpointConfigEntity cloneEndpointConfig = originalEndpointConfigEntity; // Clona o endpoint de configuração para não alterar o original

        Map<Object, Object> results = null;
        if (!isParametrized) {
            try {
                results = (Map<Object, Object>) getRequestService(provider.getApiType()).execute(provider, cloneEndpointConfig);
            } catch (Exception e) {
                logger.error("Erro ao processar versões para o fornecedor: {}", provider.getName(), e);
                throw new RuntimeException(e);
            }
        }
        else {
            cloneEndpointConfig = cloneEndpointConfig(originalEndpointConfigEntity);
        }

        for (ProviderModelsEntity model : models) {

            if (isParametrized) {

                try {
                    updateEndpointConfigWithModelInfo(cloneEndpointConfig, model);
                    results = (Map<Object, Object>) getRequestService(provider.getApiType()).execute(provider, cloneEndpointConfig);
                } catch (Exception e) {
                    logger.error("Erro ao processar modelos para o fornecedor: {} - model: {}", provider.getName(), model.getName(), e);
                    throw new RuntimeException(e);
                }
            }

            processEachTrim(provider, model, cloneEndpointConfig, results);
        }
    }

    private EndpointConfigEntity updateEndpointConfigWithModelInfo(final EndpointConfigEntity endpointConfigEntity, final ProviderModelsEntity model) {
        final Map<String, String> replacements = Map.of(
                MODEL_ID.getNormalizedValue(), model.getExternalId(),
                BRAND_ID.getNormalizedValue(), model.getParentProviderCatalog().getExternalId()
        );

        replaceEndpointConfigFields(endpointConfigEntity, replacements);
        return endpointConfigEntity;
    }

    private void replaceEndpointConfigFields(final EndpointConfigEntity endpointConfigEntity, final Map<String, String> replacements) {
        replacements.forEach((key, value) -> {
            replaceInUrl(endpointConfigEntity, key, value);
            replaceInHeaders(endpointConfigEntity, key, value);
            replaceInAdditionalParams(endpointConfigEntity, key, value);
            replaceInPayload(endpointConfigEntity, key, value);
        });
    }

    private void processEachTrim(final ProviderEntity provider, final ProviderModelsEntity model, final EndpointConfigEntity endpointConfigEntity, final Map<Object, Object> results) {

        if (results != null && !results.isEmpty()) {
            processAndSaveCatalog(results,
                    endpointConfigEntity,
                    provider,
                    ProviderTrimsEntity.class,
                    trimRepository.findAllByModelId(model.getBaseModel().getId()),
                    model,
                    providerTrimsRepository.findAllByParentProviderCatalog(model.getId()),
                    providerModelsRepository,
                    endpointConfigEntity.getCategory());
        }
    }

    private void replaceInUrl(final EndpointConfigEntity endpointConfigEntity, final String key, final String value) {
        String updatedUrl = endpointConfigEntity.getUrl().replace("{".concat(key).concat("}"), value);
        endpointConfigEntity.setUrl(updatedUrl);
    }

    private void replaceInHeaders(final EndpointConfigEntity endpointConfigEntity, final String key, final String value) {
        if (endpointConfigEntity.getHeaders() != null && !endpointConfigEntity.getHeaders().isNull()) {
            Iterator<String> fieldNames = endpointConfigEntity.getHeaders().fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                String fieldValue = endpointConfigEntity.getHeaders().get(fieldName).textValue();
                if (fieldValue.contains("{".concat(key).concat("}"))) {
                    ((ObjectNode) endpointConfigEntity.getHeaders()).put(fieldName, fieldValue.replace("{".concat(key).concat("}"), value));
                }
            }
        }
    }

    private void replaceInAdditionalParams(final EndpointConfigEntity endpointConfigEntity, final String key, final String value) {
        JsonNode params = endpointConfigEntity.getAdditionalParams();
        if (params != null && params.isObject()) {
            ObjectNode paramsObj = (ObjectNode) params;
            paramsObj.fieldNames().forEachRemaining(fieldName -> {
                String fieldValue = paramsObj.get(fieldName).asText();
                String updatedValue = fieldValue.replace("{" + key + "}", value);
                paramsObj.put(fieldName, updatedValue);
            });
        }
    }

    private void replaceInPayload(EndpointConfigEntity endpointConfigEntity, String key, String value) {
        JsonNode payload = endpointConfigEntity.getPayload();
        if (payload != null && payload.isTextual()) {
            String updatedPayload = payload.asText().replace("{" + key + "}", value);
            endpointConfigEntity.setPayload(new TextNode(updatedPayload));
        }
    }

    /**
     * Processa e salva os dados do catalogo do fornecedor
     *
     * @param data Dados do catalogo do fornecedor
     * @param endpointConfigEntity Endpoint de configuração
     * @param provider Fornecedor
     * @param providerCatalogClassType Tipo da classe do item do catalogo do fornecedor (Brand, Model, Trim)
     * @param catalogEntities Lista de dos itens do catalogo do fornecedor
     * @param parentProviderCatalog Item pai do catalogo do fornecedor
     * @param providerCatalogList Lista de dos itens do catalogo do fornecedor
     * @param providerCatalogRepository Repositorio do item do catalogo do fornecedor
     */
    private void processAndSaveCatalog(@NotNull final Map<Object, Object> data,
                                       @NotNull final EndpointConfigEntity endpointConfigEntity,
                                       @NotNull final ProviderEntity provider,
                                       @NotNull final Class<? extends ProviderCatalogEntity> providerCatalogClassType,
                                       @NotNull final List<? extends CatalogEntity> catalogEntities,
                                       @Null final ProviderCatalogEntity parentProviderCatalog,
                                       @NotNull final List<? extends ProviderCatalogEntity> providerCatalogList,
                                       @NotNull final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository,
                                       final EndpointCategory endpointCategory) {

        logger.info("Processando e salvando dados do catalogo do fornecedor: {} para {} que pertence a: {} ", provider.getName(), endpointCategory.getDisplayName(), parentProviderCatalog != null ? parentProviderCatalog.getExternalId() : StringUtils.EMPTY);

        //Extrai a lista raiz de itens de catalogo do retorno do fornecedor
        final List list = getIdAndNameFromNestedMap(endpointConfigEntity.getResponseMapping(), data);

        if (list instanceof List && !list.isEmpty()){

            list.forEach(map -> processReturnMap(endpointConfigEntity,
                    list,
                    providerCatalogClassType,
                    catalogEntities,
                    providerCatalogList,
                    parentProviderCatalog,
                    provider,
                    providerCatalogRepository,
                    endpointCategory));

        } else if (list instanceof Map) {

            //processReturnMap(endpointConfigEntity, (Map<Object, Object>) list, providerCatalogClassType, catalogEntities, providerCatalogList, parentProviderCatalog, provider, providerCatalogRepository);

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
            }

        }

        logger.info("Dados do catalogo do fornecedor {} para {} foram processados e salvos", provider.getName(), endpointCategory.getDisplayName());
    }


    /**
     * Processa o mapa de retorno do fornecedor
     *
     * @param endpointConfigEntity Endpoint de autenticação
     * @param mapList Lista de mapas de retorno
     * @param providerCatalogClassType Tipo da classe do item do catalogo do fornecedor (Brand, Model, Trim)
     * @param catalogEntityList Lista de dos itens do catalogo do fornecedor
     * @param providerCatalogList Lista de dos itens do catalogo do fornecedor
     * @param parentProviderCatalog Item pai do catalogo do fornecedor
     * @param provider Fornecedor
     * @param providerCatalogRepository Repositorio do item do catalogo do fornecedor
     */
    private void processReturnMap(@NotNull final EndpointConfigEntity endpointConfigEntity,
                                  @NotNull final List<Map<String, Object>> mapList,
                                  @NotNull final Class<? extends ProviderCatalogEntity> providerCatalogClassType,
                                  @NotNull final List<? extends CatalogEntity> catalogEntityList,
                                  @NotNull final List<? extends ProviderCatalogEntity> providerCatalogList,
                                  @Null final ProviderCatalogEntity parentProviderCatalog,
                                  @NotNull final ProviderEntity provider,
                                  @NotNull final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository,
                                  @NotNull final EndpointCategory endpointCategory) {

        final List<ProviderCatalogEntity> providerCatalogsToSave = new ArrayList<>();

        if (mapList == null) {
            return;
        }

        final List<Map<String, Object>> maps = parentProviderCatalog != null ? mapList.stream().filter(m -> m != null && m.getOrDefault(ResponseMapping.FieldMapping.PARENT_ID.getValue(), StringUtils.EMPTY).equals(parentProviderCatalog.getExternalId())).collect(Collectors.toList()) : mapList;
        for (Map map : maps) {

            final String externalId = map.getOrDefault(ResponseMapping.FieldMapping.EXTERNAL_ID.getValue(), StringUtils.EMPTY).toString();
            final String name = map.getOrDefault(ResponseMapping.FieldMapping.NAME.getValue(), StringUtils.EMPTY).toString();

            final CatalogEntity entity = endpointConfigEntity.getCategory().getFinderInstance().find(catalogEntityList, name);
            if (entity != null) {
                final ProviderCatalogEntity providerCatalog = findOrCreateProviderCatalog(externalId, (List<ProviderCatalogEntity>) providerCatalogList, providerCatalogClassType);
                providerCatalog.setName(name);
                providerCatalog.setExternalId(externalId);
                providerCatalog.setBaseCatalog(entity);
                providerCatalog.setParentProviderCatalog(parentProviderCatalog);
                providerCatalog.setProvider(provider);
                providerCatalogsToSave.add(providerCatalog);
            }
        }

        // Salva os itens do catalogo do fornecedor
        final List<ProviderCatalogEntity> saved = ((JpaRepository)providerCatalogRepository).saveAll(providerCatalogsToSave);
        // Salva os itens do catalogo do fornecedor no cache
        saved.stream().forEach(providerCatalog -> cacheService.putInCache(CacheNames.PROVIDER_CATALOG.concat("_").concat(endpointCategory.name()), providerCatalog.getCacheKey(), providerCatalogsToSave));
    }

    /**
     * Encontra ou cria um item do catalogo do fornecedor
     *
     * @param externalId ID do item do catalogo do fornecedor
     * @param providerCatalogs Lista de itens do catalogo do fornecedor
     * @param classType Tipo da classe do item do catalogo do fornecedor
     * @return Item do catalogo do fornecedor
     */
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
