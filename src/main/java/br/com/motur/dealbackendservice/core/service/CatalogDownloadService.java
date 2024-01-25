package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import br.com.motur.dealbackendservice.core.model.common.ReturnMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatalogDownloadService {

    private final ProviderRepository providerRepository;
    private final RestTemplate restTemplate;
    private final EndpointConfigRepository endpointConfigRepository;
    private final RequestRestService requestRestService;
    private final RequestSoapService requestSoapService;
    private final ProviderBrandsRepository providerBrandsRepository;
    private final ProviderModelsRepository providerModelsRepository;
    private final ProviderTrimsRepository providerTrimsRepository;
    private final BrandRepository brandRepository;

    private final ModelRepository modelRepository;

    private final ObjectMapper objectMapper;

    public CatalogDownloadService(ProviderRepository providerRepository, RestTemplate restTemplate, EndpointConfigRepository endpointConfigRepository, RequestRestService requestRestService, RequestSoapService requestSoapService, ProviderBrandsRepository providerBrandsRepository, ProviderModelsRepository providerModelsRepository, ProviderTrimsRepository providerTrimsRepository, BrandRepository brandRepository, ModelRepository modelRepository, ObjectMapper objectMapper) {
        this.providerRepository = providerRepository;
        this.restTemplate = restTemplate;
        this.endpointConfigRepository = endpointConfigRepository;
        this.requestRestService = requestRestService;
        this.requestSoapService = requestSoapService;
        this.providerBrandsRepository = providerBrandsRepository;
        this.providerModelsRepository = providerModelsRepository;
        this.providerTrimsRepository = providerTrimsRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Download the catalog data from all providers
     */
    public void downloadCatalogData() {

        final List<ProviderEntity> providers = providerRepository.findAll();
        for (ProviderEntity provider : providers) {

            final List<EndpointConfig> authEndpoint = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, provider);

            //downloadBrandsCatalog(provider, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);
            downloadModelsCatalog(provider, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);
        }
    }

    private Object getValueFromNestedMap(final ReturnMapping.Config fieldConfig, Map<Object, Object> origin) {

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
                processAndSaveCatalog(results, endpointConfig, provider, brandRepository.findAll(), null, providerBrandsRepository.findAllByProvider(provider), BrandEntity.class, ProviderBrands.class, providerBrandsRepository);
            }
        }
    }

    /**
     * Download the models catalog from the fornecedor
     * @param provider Provedor
     * @param authEndpoint Endpoint de autenticação
     */
    private void downloadModelsCatalog(final ProviderEntity provider, final EndpointConfig authEndpoint) {
        final List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_MODELS, provider);
        for (EndpointConfig endpointConfig : catalogEndpoints) {

            //Busca todas as marcas
            var brands = providerBrandsRepository.findAll();

            brands.forEach(brand -> {

                //set the brand id in the url
                endpointConfig.setUrl(endpointConfig.getUrl().replace("{brandId}", brand.getExternalId()));

                //set the brand id in the headers
                final Map<Object,Object> headers = new HashMap<>();
                if (endpointConfig.getHeaders() != null){

                    Iterator<Map.Entry<String, JsonNode>> fieldsIterator = endpointConfig.getHeaders().fields();
                    while (fieldsIterator.hasNext()) {
                        Map.Entry<String, JsonNode> field = fieldsIterator.next();
                        headers.put(field.getKey(), objectMapper.convertValue(field.getValue(), Object.class));
                    }
                    headers.put("brandId", brand.getExternalId());
                    endpointConfig.setHeaders(objectMapper.valueToTree(headers));
                }

                //set the brand id in the additional params
                if(endpointConfig.getAdditionalParams() != null){

                    final Map<Object,Object> additionalParams = new HashMap<>();
                    Iterator<Map.Entry<String, JsonNode>> fieldsIterator = endpointConfig.getAdditionalParams().fields();
                    while (fieldsIterator.hasNext()) {
                        Map.Entry<String, JsonNode> field = fieldsIterator.next();
                        additionalParams.put(field.getKey(), objectMapper.convertValue(field.getValue(), Object.class));
                    }
                    additionalParams.put("brandId", brand.getExternalId());
                    endpointConfig.setAdditionalParams(objectMapper.valueToTree(additionalParams));
                }

                //set the brand id in the payload
                if (endpointConfig.getPayload() != null){

                    final Map<Object,Object> payload = new HashMap<>();
                    Iterator<Map.Entry<String, JsonNode>> fieldsIterator = endpointConfig.getPayload().fields();
                    while (fieldsIterator.hasNext()) {
                        Map.Entry<String, JsonNode> field = fieldsIterator.next();
                        payload.put(field.getKey(), objectMapper.convertValue(field.getValue(), Object.class));
                    }
                    payload.put("brandId", brand.getExternalId());
                    endpointConfig.setPayload(objectMapper.valueToTree(payload));
                }

                Map<Object, Object> results = (Map) requestRestService.execute(provider, endpointConfig, null);
                if (results != null && !results.isEmpty()) {
                    processAndSaveCatalog(results, endpointConfig, provider, modelRepository.findAllByBrand(brand.getBaseCatalog()), brand, providerModelsRepository.findAllByParentProviderCatalog(brand), ModelEntity.class, ProviderModels.class, providerModelsRepository);
                }
            });

        }
    }

    /**
     * Download the trims catalog from the fornecedor
     *
     * @param data                Dados recebidos do fornecedor
     * @param endpointConfig      Endpoint de autenticação
     * @param provider            Provedor
     * @param providerCatalogList Lista de dos itens do catalogo do fornecedor
     */
    private void processAndSaveCatalog(final Map<Object, Object> data,
                                       final EndpointConfig endpointConfig,
                                       final ProviderEntity provider,
                                       final List<? extends CatalogEntity> catalogEntities,
                                       final ProviderCatalogEntity parentProviderCatalog,
                                       final List<? extends ProviderCatalogEntity> providerCatalogList,
                                       final Class<? extends CatalogEntity> catalogEntityClass,
                                       final Class<? extends ProviderCatalogEntity> providerCatalogEntityClass,
                                       final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        //Extrai a lista raiz de itens de catalogo do retorno do fornecedor
        final Object list = getValueFromNestedMap(endpointConfig.getReturnMapping().getRoot(), data);

        if (endpointConfig.getReturnMapping() != null && endpointConfig.getReturnMapping().getRoot() != null) {

            final ReturnMapping.Config root = endpointConfig.getReturnMapping().getRoot();
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

                    brandList.forEach(brandMap -> processReturnMap(mapList, catalogEntities, providerCatalogList, parentProviderCatalog, provider, endpointConfig.getReturnMapping().getFieldMappings(), catalogEntityClass, providerCatalogEntityClass, providerCatalogRepository));
                }

            } else if (root.getOriginDatatype() == DataType.MAP) {

                processReturnMap((Map<Object, Object>) list, catalogEntities, providerCatalogList, parentProviderCatalog, provider, endpointConfig.getReturnMapping().getFieldMappings(), catalogEntityClass, providerCatalogEntityClass, providerCatalogRepository);
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
                            //throw new RuntimeException(exc);
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

                    processReturnMap(mapList, catalogEntities, providerCatalogList, parentProviderCatalog, provider, endpointConfig.getReturnMapping().getFieldMappings(), catalogEntityClass, providerCatalogEntityClass, providerCatalogRepository);
                }

            }

        }

    }


    /**
     * Process the return map from the provider and save the data in the database
     *
     * @param brandMap
     * @param catalogEntityList
     * @param providerCatalogList
     * @param parentProviderCatalog
     * @param provider
     * @param fieldMappings
     * @param catalogEntityClass
     * @param providerCatalogEntityClass
     * @param providerCatalogRepository
     */
    private void processReturnMap(final Map<Object, Object> brandMap,
                                  final List<? extends CatalogEntity> catalogEntityList,
                                  final List<? extends ProviderCatalogEntity> providerCatalogList,
                                  final ProviderCatalogEntity parentProviderCatalog, final ProviderEntity provider,
                                  final List<ReturnMapping.Config> fieldMappings,
                                  final Class<? extends CatalogEntity> catalogEntityClass,
                                  final Class<? extends ProviderCatalogEntity> providerCatalogEntityClass,
                                  final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        final ReturnMapping.Config externalIDConfig = fieldMappings.stream().filter(config -> config.getDestination() == ReturnMapping.FieldMapping.EXTERNAL_ID).findFirst().orElse(null);
        final ReturnMapping.Config nameConfig = fieldMappings.stream().filter(config -> config.getDestination() == ReturnMapping.FieldMapping.NAME).findFirst().orElse(null);

        brandMap.forEach((key, value) -> {

            catalogEntityList.stream()
                    .filter(brandEntity -> brandEntity.getName().trim().equalsIgnoreCase(nameConfig.getOriginPath().contains("#key") ? key.toString().trim() : value.toString().trim()) ||
                            ArrayUtils.contains(brandEntity.getSynonymsArray(), nameConfig.getOriginPath().contains("#key") ? key.toString().trim() : value.toString().trim()))
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

                        final ProviderCatalogEntity providerCatalog = findOrCreateProviderCatalog(externalId, providerCatalogList.toArray(), providerCatalogEntityClass);
                        providerCatalog.setName(name);
                        providerCatalog.setExternalId(externalId);
                        providerCatalog.setBaseCatalog(entity);
                        providerCatalog.setParentProviderCatalog(parentProviderCatalog);
                        providerCatalog.setProvider(provider);
                        ((JpaRepository)providerCatalogRepository).save(providerCatalog);
                    });
        });

    }

    private ProviderCatalogEntity findOrCreateProviderCatalog(final String externalId, final Object[] providerBrands, final Class<? extends ProviderCatalogEntity> providerCatalogEntityClass) {

        try {
            Constructor<?> constructor = providerCatalogEntityClass.getDeclaredConstructor();
            return (ProviderCatalogEntity) Arrays.stream(providerBrands).toList().stream().filter(p ->((ProviderCatalogEntity) p).getExternalId().equals(externalId)).findFirst().orElse(constructor.newInstance());
        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException | NoSuchMethodException e) {

        }
        return null;
    }


    private void downloadModelsCatalog(){

    }

    private void downloadTrimsCatalog(){

    }

}
