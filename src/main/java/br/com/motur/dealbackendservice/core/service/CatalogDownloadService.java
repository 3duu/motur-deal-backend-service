package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.ResponseProcessor;
import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
     * Download the catalog data from all providers
     */
    public void downloadCatalogData() {

        logger.info("Downloading catalog data from providers");
        final List<ProviderEntity> providers = providerRepository.findAllAutoDownloadCatalog();
        for (ProviderEntity provider : providers) {

            logger.info("Downloading catalog from provider: " + provider.getName());
            final List<EndpointConfig> authEndpoint = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, provider);

            downloadBrandsCatalog(provider, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);
            downloadModelsCatalog(provider, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);
            downloadTrimsCatalog(provider, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);

            logger.info("Downloaded catalog from provider: " + provider.getName());
        }
    }

    /**
     * Obter o valor de um campo de um HashMap aninhado
     * @param fieldConfig Configuração do campo
     * @param origin Mapa aninhado de origem
     */
    private Object getValueFromNestedMap(final ResponseMapping.Config fieldConfig, Map<Object, Object> origin) {

        if (fieldConfig == null) {
            return origin;
        }

        final String[] splitKeys = fieldConfig.getOriginPath().trim().replace("#key","").replace("#value","").split("\\.");
        for (int i = 0; i < splitKeys.length - 1; i++) {
            origin = (Map<Object, Object>) origin.get(splitKeys[i]);
            if (origin == null) {
                return origin;
            }
        }

        return origin.get(splitKeys[splitKeys.length - 1]);
    }

    /**
     * Download das marcas do fornecedor
     * @param provider
     * @param authEndpoint Endpoint de autenticação
     */
    private void downloadBrandsCatalog(final ProviderEntity provider, final EndpointConfig authEndpoint) {
        final List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_BRANDS, provider);
        for (EndpointConfig endpointConfig : catalogEndpoints) {
            Map<Object, Object> results = (Map) requestRestService.execute(provider, endpointConfig, null);
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
    private void downloadModelsCatalog(final ProviderEntity provider, final EndpointConfig authEndpoint) {
        final List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_MODELS, provider);
        for (final EndpointConfig originalEndpointConfig : catalogEndpoints) {

            //Busca todas as marcas
            final List<ProviderBrands> brands = providerBrandsRepository.findAllByProviderId(provider.getId());

            brands.forEach(brand -> {

                EndpointConfig endpointConfig = null;
                try {
                    endpointConfig = objectMapper.readValue(objectMapper.writeValueAsString(originalEndpointConfig), EndpointConfig.class);
                } catch (JsonProcessingException e) {
                    logger.error("Error parsing endpoint config: " + e.getMessage(), e);
                    endpointConfig = modelMapper.map(originalEndpointConfig, EndpointConfig.class);
                }

                logger.info("Downloading models from brand: " + brand.getName());

                //set the brand id in the url
                endpointConfig.setUrl(endpointConfig.getUrl().replace("{brandId}", brand.getExternalId()));

                //set the brand id in the headers
                if (endpointConfig.getHeaders() != null){

                    endpointConfig.setHeaders(formatJsonField(endpointConfig.getHeaders(), Map.of("brandId", brand.getExternalId())));
                }

                //set the brand id in the additional params
                if(endpointConfig.getAdditionalParams() != null){

                    endpointConfig.setAdditionalParams(formatJsonField(endpointConfig.getAdditionalParams(), Map.of("brandId", brand.getExternalId())));
                }

                //set the brand id in the payload
                if (endpointConfig.getPayload() != null){

                    endpointConfig.setPayload(formatJsonField(endpointConfig.getPayload(), Map.of("brandId", brand.getExternalId())));
                }

                final Map<Object, Object> results = (Map) requestRestService.execute(provider, endpointConfig, null);
                if (results != null && !results.isEmpty()) {
                    processAndSaveCatalog(results, endpointConfig, provider, ProviderModels.class, modelRepository.findAllByBrand(brand.getBaseCatalog().getId()), brand, providerModelsRepository.findAllByParentProviderCatalog(brand), providerModelsRepository);
                }

                logger.info("Downloaded models from brand: " + brand.getName());
            });

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
     * Download the versões catalogo do fornecedor
     *
     * @param provider Provedor/Fornecedor
     * @param authEndpoint Endpoint de autenticação (opcional)
     */
    private void downloadTrimsCatalog(final ProviderEntity provider, final EndpointConfig authEndpoint){

            final List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_TRIMS, provider);
            for (final EndpointConfig originalEndpointConfig : catalogEndpoints) {

                //Busca todos os modelos
                final List<ProviderModels> models = providerModelsRepository.findAllByProviderId(provider.getId());

                models.forEach(model -> {

                    EndpointConfig endpointConfig = null;
                    try {
                        endpointConfig = objectMapper.readValue(objectMapper.writeValueAsString(originalEndpointConfig), EndpointConfig.class);
                    } catch (JsonProcessingException e) {
                        logger.error("Error parsing endpoint config: " + e.getMessage(), e);
                        endpointConfig = modelMapper.map(originalEndpointConfig, EndpointConfig.class);
                    }

                    logger.info("Downloading trims from model: " + model.getName());

                    //set the brand id in the url
                    endpointConfig.setUrl(endpointConfig.getUrl().replace("{modelId}", model.getExternalId()));
                    endpointConfig.setUrl(endpointConfig.getUrl().replace("{brandId}", model.getParentProviderCatalog().getExternalId()));


                    //set the brand id in the headers
                    if (endpointConfig.getHeaders() != null){

                        endpointConfig.setHeaders(formatJsonField(endpointConfig.getHeaders(), new HashMap<>(){
                            {
                                put("modelId", model.getExternalId());
                                put("brandId", model.getParentProviderCatalog().getExternalId());
                            }
                        }));
                    }

                    //set the brand id in the additional params
                    if(endpointConfig.getAdditionalParams() != null){

                        endpointConfig.setAdditionalParams(formatJsonField(endpointConfig.getAdditionalParams(), new HashMap<>(){
                            {
                                put("modelId", model.getExternalId());
                                put("brandId", model.getParentProviderCatalog().getExternalId());
                            }
                        }));
                    }

                    //set the brand id in the payload
                    if (endpointConfig.getPayload() != null){

                        endpointConfig.setPayload(formatJsonField(endpointConfig.getPayload(), new HashMap<>(){
                            {
                                put("modelId", model.getExternalId());
                                put("brandId", model.getParentProviderCatalog().getExternalId());
                            }
                        }));
                    }

                    final Map<Object, Object> results = (Map) requestRestService.execute(provider, endpointConfig, null);
                    if (results != null && !results.isEmpty()) {
                        processAndSaveCatalog(results, endpointConfig, provider, ProviderTrims.class, trimRepository.findAllByModelId(model.getBaseModel().getId()), model, providerTrimsRepository.findAllByParentProviderCatalog(model), providerModelsRepository);
                    }

                    logger.info("Downloaded trims from model: " + model.getName());

                });

            }
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

        logger.info("Processing and saving catalog data from provider: " + provider.getName());

        //Extrai a lista raiz de itens de catalogo do retorno do fornecedor
        final Object list = getValueFromNestedMap(/*endpointConfig.getResponseMapping().getRoot()*/null, data);

        if (endpointConfig.getResponseMapping() != null /*&& endpointConfig.getResponseMapping().getRoot() != null*/) {

            final ResponseMapping.Config root = /*endpointConfig.getResponseMapping().getRoot();*/null;
            if (root.getOriginDatatype() == DataType.LIST) {

                final List brandList = (List) list;
                if (!brandList.isEmpty() && brandList.get(0) instanceof Map){

                    final List<Map<Object, Object>> listMap = (List<Map<Object, Object>>) list;

                    final Map<Object, Object> mapList = listMap.stream()
                            .flatMap(m -> m.entrySet().stream())
                            .collect(Collectors.toMap(
                                    entry -> entry.getKey(),
                                    entry -> entry.getValue(),
                                    (v1, v2) -> v1
                            ));

                    brandList.forEach(brandMap -> processReturnMap(endpointConfig, mapList, providerCatalogClassType, catalogEntities, providerCatalogList, parentProviderCatalog, provider, endpointConfig.getResponseMapping().getFieldMappings(), providerCatalogRepository));
                }

            } else if (root.getOriginDatatype() == DataType.MAP) {

                processReturnMap(endpointConfig, (Map<Object, Object>) list, providerCatalogClassType, catalogEntities, providerCatalogList, parentProviderCatalog, provider, endpointConfig.getResponseMapping().getFieldMappings(), providerCatalogRepository);
            } else if (root.getOriginDatatype() == DataType.JSON) {

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
                            logger.error("Error parsing json node: " + exc.getMessage(), exc);
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

                    processReturnMap(endpointConfig, mapList, providerCatalogClassType, catalogEntities, providerCatalogList, parentProviderCatalog, provider, endpointConfig.getResponseMapping().getFieldMappings(), providerCatalogRepository);
                }

            }

        }

        logger.info("Processed and saved catalog data from provider: " + provider.getName());

    }


    /**
     * Process the return map from the provider and save the data in the database
     *
     * @param endpointConfig Endpoint de autenticação
     * @param brandMap Mapa de retorno do fornecedor
     * @param providerCatalogClassType Tipo da classe do item do catalogo do fornecedor (Brand, Model, Trim)
     * @param catalogEntityList Lista de dos itens do catalogo do fornecedor
     * @param providerCatalogList Lista de dos itens do catalogo do fornecedor
     * @param parentProviderCatalog Item pai do catalogo do fornecedor
     * @param provider Fornecedor
     * @param fieldMappings Lista de mapeamentos de campos
     * @param providerCatalogRepository Repositorio do item do catalogo do fornecedor
     */
    private void processReturnMap(final EndpointConfig endpointConfig,
                                  final Map<Object, Object> brandMap,
                                  final Class<? extends ProviderCatalogEntity> providerCatalogClassType,
                                  final List<? extends CatalogEntity> catalogEntityList,
                                  final List<? extends ProviderCatalogEntity> providerCatalogList,
                                  final ProviderCatalogEntity parentProviderCatalog,
                                  final ProviderEntity provider,
                                  final List<ResponseMapping.Config> fieldMappings,
                                  final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        final ResponseMapping.Config externalIDConfig = fieldMappings.stream().filter(config -> config.getDestination() == ResponseMapping.FieldMapping.EXTERNAL_ID).findFirst().orElse(null);
        final ResponseMapping.Config nameConfig = fieldMappings.stream().filter(config -> config.getDestination() == ResponseMapping.FieldMapping.NAME).findFirst().orElse(null);

        brandMap.forEach((key, value) -> {

            catalogEntityList.stream()
                    .filter(entity -> endpointConfig.getCategory().getFinderInstance().find(entity, nameConfig.getOriginPath().contains("#key") ? key.toString().trim() : value.toString().trim()))
                    .findFirst()
                    .ifPresent(entity -> {

                        String externalId = null;
                        String name = null;

                        if (externalIDConfig != null) {

                            DataType datatype = externalIDConfig.getOriginDatatype();
                            if (datatype != null) {

                                if (datatype == DataType.MAP) {
                                    var returnValue = getValueFromNestedMap(externalIDConfig, externalIDConfig.getOriginPath().contains("#key") ? (Map<Object, Object>) key : (externalIDConfig.getOriginPath().contains("#value") ? (Map<Object, Object>) value : new HashMap<>()));
                                    externalId = returnValue != null ? returnValue.toString() : null;

                                } else if (datatype == DataType.JSON) {

                                } else if (datatype == DataType.STRING || datatype == DataType.INT || datatype == DataType.LONG || datatype == DataType.CHAR || datatype == DataType.SHORT) {
                                    externalId = externalIDConfig.getOriginPath().contains("#key") ? key.toString() : (externalIDConfig.getOriginPath().contains("#value") ? value.toString() : null);
                                }
                            }

                            if (nameConfig != null) {

                                datatype = nameConfig.getOriginDatatype();
                                if (datatype != null) {

                                    if (datatype == DataType.MAP) {
                                        var returnValue = getValueFromNestedMap(nameConfig, nameConfig.getOriginPath().contains("#key") ? (Map<Object, Object>) key : (nameConfig.getOriginPath().contains("#value") ? (Map<Object, Object>) value : new HashMap<>()));
                                        name = returnValue != null ? returnValue.toString() : null;

                                    } else if (datatype == DataType.JSON) {

                                    } else if (datatype == DataType.STRING || datatype == DataType.INT || datatype == DataType.LONG || datatype == DataType.CHAR || datatype == DataType.SHORT) {
                                        name = nameConfig.getOriginPath().contains("#key") ? key.toString() : (nameConfig.getOriginPath().contains("#value") ? value.toString() : null);
                                    }
                                }
                            }

                        }

                        final ProviderCatalogEntity providerCatalog = findOrCreateProviderCatalog(externalId, (List<ProviderCatalogEntity>) providerCatalogList, providerCatalogClassType);
                        providerCatalog.setName(name);
                        providerCatalog.setExternalId(externalId);
                        providerCatalog.setBaseCatalog(entity);
                        providerCatalog.setParentProviderCatalog(parentProviderCatalog);
                        providerCatalog.setProvider(provider);
                        ((JpaRepository)providerCatalogRepository).save(providerCatalog);
                    });
        });

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
