package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.ResponseProcessor;
import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import br.com.motur.dealbackendservice.core.model.common.IntegrationFields;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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


    private final ApplicationContext applicationContext;
    private final ProviderRepository providerRepository;
    private final EndpointConfigRepository endpointConfigRepository;
    private final ProviderBrandsRepository providerBrandsRepository;
    private final ProviderModelsRepository providerModelsRepository;
    private final ProviderTrimsRepository providerTrimsRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final TrimRepository trimRepository;


    @Autowired
    public CatalogDownloadService(final ApplicationContext applicationContext, final ProviderRepository providerRepository, EndpointConfigRepository endpointConfigRepository,
                                  ProviderBrandsRepository providerBrandsRepository, ProviderModelsRepository providerModelsRepository,
                                  ProviderTrimsRepository providerTrimsRepository, BrandRepository brandRepository,
                                  ModelRepository modelRepository, TrimRepository trimRepository, ObjectMapper objectMapper, ModelMapper modelMapper, final ResponseProcessor responseProcessor) {
        super(applicationContext, responseProcessor, objectMapper, modelMapper);
        this.applicationContext = applicationContext;
        this.providerRepository = providerRepository;
        this.endpointConfigRepository = endpointConfigRepository;
        this.providerBrandsRepository = providerBrandsRepository;
        this.providerModelsRepository = providerModelsRepository;
        this.providerTrimsRepository = providerTrimsRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.trimRepository = trimRepository;
    }


    /**
     * Baixando catálogo de todos os fornecedores
     */
    public void downloadCatalogData() {

        logger.info("Baixando catálogo de todos os fornecedores");
        final List<ProviderEntity> providers = providerRepository.findAllAutoDownloadCatalog();
        for (ProviderEntity provider : providers) {

            logger.info("Baixando catálogo do fornecedor: {}",provider.getName());

            downloadBrandsCatalog(provider); // Baixando marcas
            downloadModelsCatalog(provider); // Baixando modelos
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
                processAndSaveCatalog(results, endpointConfigEntity, provider, ProviderBrands.class, brandRepository.findAll(), null, providerBrandsRepository.findAllByProvider(provider), providerBrandsRepository);
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
        final List<ProviderBrands> brands = providerBrandsRepository.findAllByProviderId(provider.getId());
        EndpointConfigEntity cloneEndpointConfig = originalEndpointConfigEntity;

        Map<Object, Object> results = null;
        if (!isParametrized) {
            try {
                results = (Map<Object, Object>) getRequestService(provider.getApiType()).execute(provider, cloneEndpointConfig);
            } catch (Exception e) {
                logger.error("Error processing models for provider: {} - {}", provider.getName(), e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        else {
            cloneEndpointConfig = cloneEndpointConfig(originalEndpointConfigEntity);
        }

        for (final ProviderBrands brand : brands) {

            if (isParametrized) {

                try {
                    bindExternalIdToEndpointConfig(cloneEndpointConfig, brand, BRAND_ID);
                    results = (Map<Object, Object>) getRequestService(provider.getApiType()).execute(provider, cloneEndpointConfig);
                } catch (Exception e) {
                    logger.error("Error processing models for provider: {} - brand {}", provider.getName(), brand.getName(), e);
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
    private void updateAndProcessEndpointForBrand(final ProviderEntity provider, final ProviderBrands brand, final EndpointConfigEntity endpointConfigEntity, final Map<Object, Object> results) {
        try {

            if (results != null && !results.isEmpty()) {
                final List<ModelEntity> models = brand != null ? modelRepository.findAllByBrand(brand.getBaseCatalog().getId()) : modelRepository.findAll();
                processAndSaveCatalog(results, endpointConfigEntity, provider, ProviderModelsEntity.class, models, brand, providerModelsRepository.findAllByParentProviderCatalog(brand), providerModelsRepository);
            }
        } catch (Exception e) {
            logger.error("Error processing models for brand: {} - {}", brand.getName(), e.getMessage(), e);
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

    private void processEachModel(ProviderEntity provider, EndpointConfigEntity originalEndpointConfigEntity) {

        final boolean isParametrized = responseProcessor.isParametrizedEndpoint(originalEndpointConfigEntity); // Verifica se o endpoint é parametrizado, caso seja, será feita várias chamadas e o valor do parâmetro é substituído em cada uma delas
        final List<ProviderModelsEntity> models = providerModelsRepository.findAllByProviderId(provider.getId());
        EndpointConfigEntity cloneEndpointConfig = originalEndpointConfigEntity;

        Map<Object, Object> results = null;
        if (!isParametrized) {
            try {
                results = (Map<Object, Object>) getRequestService(provider.getApiType()).execute(provider, cloneEndpointConfig);
            } catch (Exception e) {
                logger.error("Error processing trims for provider: {}", provider.getName(), e);
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
                    logger.error("Error processing trims for provider: {} - model: {}", provider.getName(), model.getName(), e);
                    throw new RuntimeException(e);
                }
            }

            processEachTrim(provider, model, cloneEndpointConfig, results);
        }
    }

    private EndpointConfigEntity updateEndpointConfigWithModelInfo(EndpointConfigEntity endpointConfigEntity, ProviderModelsEntity model) {
        Map<String, String> replacements = Map.of(
                MODEL_ID.getNormalizedValue(), model.getExternalId(),
                BRAND_ID.getNormalizedValue(), model.getParentProviderCatalog().getExternalId()
        );

        replaceEndpointConfigFields(endpointConfigEntity, replacements);
        return endpointConfigEntity;
    }

    private void replaceEndpointConfigFields(EndpointConfigEntity endpointConfigEntity, Map<String, String> replacements) {
        replacements.forEach((key, value) -> {
            replaceInUrl(endpointConfigEntity, key, value);
            replaceInHeaders(endpointConfigEntity, key, value);
            replaceInAdditionalParams(endpointConfigEntity, key, value);
            replaceInPayload(endpointConfigEntity, key, value);
        });
    }

    private void processEachTrim(ProviderEntity provider, ProviderModelsEntity model, EndpointConfigEntity endpointConfigEntity, Map<Object, Object> results) {

        if (results != null && !results.isEmpty()) {
            processAndSaveCatalog(results, endpointConfigEntity, provider, ProviderTrims.class, trimRepository.findAllByModelId(model.getBaseModel().getId()), model, providerTrimsRepository.findAllByParentProviderCatalog(model), providerModelsRepository);
        }
    }

    private void replaceInUrl(EndpointConfigEntity endpointConfigEntity, String key, String value) {
        String updatedUrl = endpointConfigEntity.getUrl().replace("{" + key + "}", value);
        endpointConfigEntity.setUrl(updatedUrl);
    }

    private void replaceInHeaders(EndpointConfigEntity endpointConfigEntity, String key, String value) {
        if (endpointConfigEntity.getHeaders() != null && !endpointConfigEntity.getHeaders().isNull()) {
            Iterator<String> fieldNames = endpointConfigEntity.getHeaders().fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                String fieldValue = endpointConfigEntity.getHeaders().get(fieldName).textValue();
                if (fieldValue.contains("{" + key + "}")) {
                    ((ObjectNode) endpointConfigEntity.getHeaders()).put(fieldName, fieldValue.replace("{" + key + "}", value));
                }
            }
        }
    }

    private void replaceInAdditionalParams(EndpointConfigEntity endpointConfigEntity, String key, String value) {
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
                                       @NotNull final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        logger.info("Processando e salvando dados do catalogo do fornecedor: {}",provider.getName());

        //Extrai a lista raiz de itens de catalogo do retorno do fornecedor
        final Object list = getValueFromNestedMap(endpointConfigEntity.getResponseMapping(), data);

        if (list instanceof List && !((List)list).isEmpty() && ((List)list).get(0) instanceof Map){

            final List newList = (List) list;
            final List<Map<Object, Object>> listMap = (List<Map<Object, Object>>) list;

            final Map<Object, Object> mapList = listMap.stream()
                    .flatMap(m -> m.entrySet().stream())
                    .collect(Collectors.toMap(
                            entry -> entry.getKey(),
                            entry -> entry.getValue(),
                            (v1, v2) -> v1
                    ));

            newList.forEach(map -> processReturnMap(endpointConfigEntity,
                    mapList,
                    providerCatalogClassType,
                    catalogEntities,
                    providerCatalogList,
                    parentProviderCatalog,
                    provider,
                    providerCatalogRepository));

        } else if (list instanceof Map) {

            processReturnMap(endpointConfigEntity, (Map<Object, Object>) list, providerCatalogClassType, catalogEntities, providerCatalogList, parentProviderCatalog, provider, providerCatalogRepository);

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

                processReturnMap(endpointConfigEntity,

                        mapList,
                        providerCatalogClassType,
                        catalogEntities,
                        providerCatalogList,
                        parentProviderCatalog,
                        provider,
                        providerCatalogRepository);
            }

        }

        logger.info("Dados do catalogo do fornecedor " + provider.getName() + " foram processados e salvos");
    }


    /**
     * Processa o mapa de retorno do fornecedor
     *
     * @param endpointConfigEntity Endpoint de autenticação
     * @param map Mapa de retorno do fornecedor
     * @param providerCatalogClassType Tipo da classe do item do catalogo do fornecedor (Brand, Model, Trim)
     * @param catalogEntityList Lista de dos itens do catalogo do fornecedor
     * @param providerCatalogList Lista de dos itens do catalogo do fornecedor
     * @param parentProviderCatalog Item pai do catalogo do fornecedor
     * @param provider Fornecedor
     * @param providerCatalogRepository Repositorio do item do catalogo do fornecedor
     */
    private void processReturnMap(@NotNull final EndpointConfigEntity endpointConfigEntity,
                                  @NotNull final Map<Object, Object> map,
                                  @NotNull final Class<? extends ProviderCatalogEntity> providerCatalogClassType,
                                  @NotNull final List<? extends CatalogEntity> catalogEntityList,
                                  @NotNull final List<? extends ProviderCatalogEntity> providerCatalogList,
                                  @Null final ProviderCatalogEntity parentProviderCatalog,
                                  @NotNull final ProviderEntity provider,
                                  @NotNull final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        final List<ProviderCatalogEntity> providerCatalogsToSave = new ArrayList<>();

        if (map == null) {
            return;
        }

        map.forEach((key, value) -> {

            final String externalId = key.toString();
            final String name = value.toString();

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
        });

        ((JpaRepository)providerCatalogRepository).saveAll(providerCatalogsToSave);
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
