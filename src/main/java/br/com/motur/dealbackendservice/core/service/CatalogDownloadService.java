package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.ResponseProcessor;
import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
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
            final List<EndpointConfigEntity> authEndpoint = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, provider);

            for(EndpointConfigEntity endpointConfigEntity : authEndpoint){
                downloadBrandsCatalog(provider); // Baixando marcas
                downloadModelsCatalog(provider); // Baixando modelos
                downloadTrimsCatalog(provider); // Baixando versões
            }

            /*authEndpoint.forEach(endpointConfigEntity -> {
                downloadBrandsCatalog(provider, endpointConfigEntity); // Baixando marcas
                downloadModelsCatalog(provider, endpointConfigEntity); // Baixando modelos
                downloadTrimsCatalog(provider, endpointConfigEntity); // Baixando versões
            });*/

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
        List<ProviderBrands> brands = providerBrandsRepository.findAllByProviderId(provider.getId());
        brands.forEach(brand -> updateAndProcessEndpointForBrand(provider, brand, cloneEndpointConfig(originalEndpointConfigEntity)));
    }

    private void updateAndProcessEndpointForBrand(final ProviderEntity provider, final ProviderBrands brand, EndpointConfigEntity endpointConfigEntity) {
        try {
            updateEndpointConfigForBrand(endpointConfigEntity, brand);
            Map<Object, Object> results = (Map<Object, Object>) getRequestService(provider.getApiType()).execute(provider, endpointConfigEntity);
            if (results != null && !results.isEmpty()) {
                processAndSaveCatalog(results, endpointConfigEntity, provider, ProviderModelsEntity.class, modelRepository.findAllByBrand(brand.getBaseCatalog().getId()), brand, providerModelsRepository.findAllByParentProviderCatalog(brand), providerModelsRepository);
            }
        } catch (Exception e) {
            logger.error("Error processing models for brand: {} - {}", brand.getName(), e.getMessage(), e);
        }
    }

    private void updateEndpointConfigForBrand(EndpointConfigEntity endpointConfigEntity, ProviderBrands brand) {
        String brandId = brand.getExternalId();
        responseProcessor.updateEndpointConfigFields(endpointConfigEntity, BRAND_ID.getNormalizedValue(), brandId);
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

    private EndpointConfigEntity cloneEndpointConfig(EndpointConfigEntity originalEndpointConfigEntity) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(originalEndpointConfigEntity), EndpointConfigEntity.class);
        } catch (JsonProcessingException e) {
            logger.error("Error cloning endpoint config: " + e.getMessage(), e);
            return modelMapper.map(originalEndpointConfigEntity, EndpointConfigEntity.class);
        }
    }

    private void processEachModel(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity) {
        final List<ProviderModelsEntity> models = providerModelsRepository.findAllByProviderId(provider.getId());
        models.forEach(model -> {
            try {
                processEachTrim(provider, model, updateEndpointConfigWithModelInfo(endpointConfigEntity, model));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
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

    private void processEachTrim(ProviderEntity provider, ProviderModelsEntity model, EndpointConfigEntity endpointConfigEntity) throws Exception {
        final Map<Object, Object> results = (Map<Object, Object>) getRequestService(provider.getApiType()).execute(provider, endpointConfigEntity);
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
     * Download the trims catalog from the fornecedor
     *
     * @param data                Dados recebidos do fornecedor
     * @param endpointConfigEntity      Endpoint de autenticação
     * @param provider            Provedor
     * @param providerCatalogClassType Tipo da classe do item do catalogo do fornecedor
     * @param providerCatalogList Lista de dos itens do catalogo do fornecedor
     * @param parentProviderCatalog Item pai do catalogo do fornecedor
     * @param providerCatalogRepository Repository do item do catalogo do fornecedor
     */
    private void processAndSaveCatalog(final Map<Object, Object> data,
                                       final EndpointConfigEntity endpointConfigEntity,
                                       final ProviderEntity provider,
                                       final Class<? extends ProviderCatalogEntity> providerCatalogClassType,
                                       final List<? extends CatalogEntity> catalogEntities,
                                       final ProviderCatalogEntity parentProviderCatalog,
                                       final List<? extends ProviderCatalogEntity> providerCatalogList,
                                       final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        logger.info("Processando e salvando dados do catalogo do fornecedor: {}",provider.getName());

        //Extrai a lista raiz de itens de catalogo do retorno do fornecedor
        final Object list = getValueFromNestedMap(endpointConfigEntity.getResponseMapping(), data);

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

            brandList.forEach(brandMap -> processReturnMap(endpointConfigEntity,
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

                processReturnMap(endpointConfigEntity, mapList, providerCatalogClassType, catalogEntities, providerCatalogList, parentProviderCatalog, provider, providerCatalogRepository);
            }

        }

        logger.info("Dados do catalogo do fornecedor " + provider.getName() + " foram processados e salvos");

    }


    /**
     * Processa o mapa de retorno do fornecedor
     *
     * @param endpointConfigEntity Endpoint de autenticação
     * @param brandMap Mapa de retorno do fornecedor
     * @param providerCatalogClassType Tipo da classe do item do catalogo do fornecedor (Brand, Model, Trim)
     * @param catalogEntityList Lista de dos itens do catalogo do fornecedor
     * @param providerCatalogList Lista de dos itens do catalogo do fornecedor
     * @param parentProviderCatalog Item pai do catalogo do fornecedor
     * @param provider Fornecedor
     * @param providerCatalogRepository Repositorio do item do catalogo do fornecedor
     */
    private void processReturnMap(final EndpointConfigEntity endpointConfigEntity,
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
