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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final ObjectMapper objectMapper;

    public CatalogDownloadService(ProviderRepository providerRepository, RestTemplate restTemplate, EndpointConfigRepository endpointConfigRepository, RequestRestService requestRestService, RequestSoapService requestSoapService, ProviderBrandsRepository providerBrandsRepository, ProviderModelsRepository providerModelsRepository, ProviderTrimsRepository providerTrimsRepository, BrandRepository brandRepository, ObjectMapper objectMapper) {
        this.providerRepository = providerRepository;
        this.restTemplate = restTemplate;
        this.endpointConfigRepository = endpointConfigRepository;
        this.requestRestService = requestRestService;
        this.requestSoapService = requestSoapService;
        this.providerBrandsRepository = providerBrandsRepository;
        this.providerModelsRepository = providerModelsRepository;
        this.providerTrimsRepository = providerTrimsRepository;
        this.brandRepository = brandRepository;
        this.objectMapper = objectMapper;
    }

    public void downloadCatalogData() {

        final List<ProviderEntity> providers = providerRepository.findAll();
        for (ProviderEntity provider : providers) {

            final List<EndpointConfig> authEndpoint = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, provider);
            downloadBrandsCatalog(provider, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);

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

    private void downloadBrandsCatalog(final ProviderEntity provider, final EndpointConfig authEndpoint) {
        final List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_BRANDS, provider);
        for (EndpointConfig endpointConfig : catalogEndpoints) {
            Map<Object, Object> brands = (Map) requestRestService.execute(provider, endpointConfig, null);
            if (brands != null && !brands.isEmpty()) {
                processAndSaveCatalog(brands, endpointConfig, provider, brandRepository.findAll(), BrandEntity.class, ProviderBrands.class, providerBrandsRepository);
            }
        }
    }

    private void processAndSaveCatalog(final Map<Object, Object> brands,
                                       final EndpointConfig endpointConfig,
                                       final ProviderEntity provider,
                                       final List brandEntities,
                                       final Class<? extends CatalogEntity> catalogEntityClass,
                                       final Class<? extends ProviderCatalogEntity> providerCatalogEntityClass,
                                       final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        final Object list = getValueFromNestedMap(endpointConfig.getReturnMapping().getRoot(), brands);

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

                    brandList.forEach(brandMap -> processReturnMap(mapList, brandEntities, provider, endpointConfig.getReturnMapping().getFieldMappings(), catalogEntityClass, providerCatalogEntityClass, providerCatalogRepository));
                }

            } else if (root.getOriginDatatype() == DataType.MAP) {

                processReturnMap((Map<Object, Object>) list, brandEntities, provider, endpointConfig.getReturnMapping().getFieldMappings(), catalogEntityClass, providerCatalogEntityClass, providerCatalogRepository);
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

                    processReturnMap(mapList, brandEntities, provider, endpointConfig.getReturnMapping().getFieldMappings(), catalogEntityClass, providerCatalogEntityClass, providerCatalogRepository);
                }

            }

        }

    }


    /**
     * Process the return map from the provider and save the data in the database
     * @param brandMap
     * @param catalogEntityList
     * @param provider
     * @param fieldMappings
     * @param catalogEntityClass
     * @param providerCatalogEntityClass
     * @param providerCatalogRepository
     */
    private void processReturnMap(final Map<Object, Object> brandMap,
                                  final List<CatalogEntity> catalogEntityList,
                                  final ProviderEntity provider,
                                  final List<ReturnMapping.Config> fieldMappings,
                                  final Class<? extends CatalogEntity> catalogEntityClass,
                                  final Class<? extends ProviderCatalogEntity> providerCatalogEntityClass,
                                  final JpaRepository<? extends ProviderCatalogEntity, Integer> providerCatalogRepository) {

        final List<ProviderBrands> providerBrands = providerBrandsRepository.findAllByProvider(provider);

        final ReturnMapping.Config externalIDConfig = fieldMappings.stream().filter(config -> config.getDestination() == ReturnMapping.FieldMapping.EXTERNAL_ID).findFirst().orElse(null);
        final ReturnMapping.Config nameConfig = fieldMappings.stream().filter(config -> config.getDestination() == ReturnMapping.FieldMapping.NAME).findFirst().orElse(null);

        brandMap.forEach((key, value) -> {

            catalogEntityList.stream()
                    .filter(brandEntity -> brandEntity.getName().trim().equalsIgnoreCase(nameConfig.getOriginPath().contains("#key") ? key.toString().trim() : value.toString().trim()) ||
                            ArrayUtils.contains(brandEntity.getSynonymsArray(), nameConfig.getOriginPath().contains("#key") ? key.toString().trim() : value.toString().trim()))
                    .findFirst()
                    .ifPresent(brandEntity -> {

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

                        final ProviderCatalogEntity providerCatalog = findOrCreateProviderCatalog(externalId, providerBrands.toArray(), providerCatalogEntityClass);
                        providerCatalog.setName(name);
                        providerCatalog.setExternalId(externalId);
                        providerCatalog.setBaseCatalog(brandEntity);
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
