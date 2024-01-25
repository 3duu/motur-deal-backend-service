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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    //@EventListener(ApplicationReadyEvent.class)
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
                processAndSaveBrands(brands, endpointConfig, provider);
            }
        }
    }

    private void processAndSaveBrands(final Map<Object, Object> brands, final EndpointConfig endpointConfig, final ProviderEntity provider) {

        final List<BrandEntity> brandEntities = brandRepository.findAll();
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

                    brandList.forEach(brandMap -> processReturnMap(mapList, brandEntities, provider, endpointConfig.getReturnMapping().getFieldMappings()));
                }

            } else if (root.getOriginDatatype() == DataType.MAP) {

                processReturnMap((Map<Object, Object>) list, brandEntities, provider, endpointConfig.getReturnMapping().getFieldMappings());
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

                    processReturnMap(mapList, brandEntities, provider, endpointConfig.getReturnMapping().getFieldMappings());
                }

            }

        }

    }


    private void processReturnMap(final Map<Object, Object> brandMap, final List<BrandEntity> brandEntities, final ProviderEntity provider, final List<ReturnMapping.Config> fieldMappings) {

        final List<ProviderBrands> providerBrands = providerBrandsRepository.findAllByProvider(provider);

        var keyset = brandMap.keySet().toArray();
        boolean keyIsString = keyset.length > 0 && keyset[0] instanceof String;

        brandMap.forEach((key, value) -> {

            brandEntities.stream()
                    .filter(brandEntity -> brandEntity.getName().equalsIgnoreCase(keyIsString ? key.toString() : value.toString()) ||
                            ArrayUtils.contains(brandEntity.getSynonymsArray(), keyIsString ? key.toString() : value.toString()))
                    .findFirst()
                    .ifPresent(brandEntity -> {

                        String externalId = null;
                        final ReturnMapping.Config externalIDConfig = fieldMappings.stream().filter(config -> config.getDestination() == ReturnMapping.FieldMapping.EXTERNAL_ID).findFirst().orElse(null);
                        if (externalIDConfig != null){

                            DataType datatype = externalIDConfig.getOriginDatatype();
                            if (datatype != null){
                                if (datatype == DataType.MAP){
                                    var returnValue = getValueFromNestedMap(externalIDConfig, externalIDConfig.getOriginPath().contains("#key") ? (Map<Object, Object>)key : (externalIDConfig.getOriginPath().contains("#value") ? (Map<Object, Object>) value : new HashMap<>()));
                                    externalId = returnValue != null ? returnValue.toString() : null;

                                }
                                else if (datatype == DataType.JSON){

                                }
                                else if (datatype == DataType.STRING || datatype == DataType.INT || datatype == DataType.LONG || datatype == DataType.CHAR || datatype == DataType.SHORT){
                                    externalId = externalIDConfig.getOriginPath().contains("#key") ? key.toString() : (externalIDConfig.getOriginPath().contains("#value") ? value.toString() : null);
                                }
                            }


                            final ReturnMapping.Config nameConfig = fieldMappings.stream().filter(config -> config.getDestination() == ReturnMapping.FieldMapping.NAME).findFirst().orElse(null);
                            if (nameConfig != null){

                                datatype = nameConfig.getOriginDatatype();
                                if (datatype != null){

                                }
                            }

                            final String[] splitKeys = externalIDConfig.getOriginPath().split("\\.");
                            for (int i = 0; i < splitKeys.length - 1; i++) {

                                final String field = splitKeys[i];
                                //final DataType datatype = config.getOriginDatatype();

                                if (field.equals("#")) {

                                    final String code = field.replace("#", "").trim().toLowerCase();

                                    if (code.equals("key")){

                                    }
                                    else if (code.equals("value")){

                                    }
                                }
                                else {

                                }
                        }

                        final String externalIdField = externalIDConfig.getOriginPath();
                        final DataType externalIdDatatype = externalIDConfig.getOriginDatatype();

                        String externalId = fieldMappings.stream().filter(config -> config.getDestination() == ReturnMapping.FieldMapping.EXTERNAL_ID).findFirst().get().getOriginPath();

                        fieldMappings.forEach(config -> {

                            final String[] splitKeys = config.getOriginPath().split("\\.");
                            for (int i = 0; i < splitKeys.length - 1; i++) {

                                final String field = splitKeys[i];
                                //final DataType datatype = config.getOriginDatatype();

                                if (field.equals("#")) {

                                    final String code = field.replace("#", "").trim().toLowerCase();

                                    if (code.equals("key")){

                                    }
                                    else if (code.equals("value")){

                                    }
                                }
                                else {


                                }

                            }

                        });



                        String name = value.toString();
                        String externalId = key.toString();
                        if (keyIsString){
                            externalId  = value.toString();
                            name = key.toString();
                        }

                        final ProviderBrands providerBrand = (ProviderBrands) findOrCreateProviderCatalog(externalId, providerBrands.toArray());
                        providerBrand.setName(name);
                        providerBrand.setExternalId(externalId);
                        providerBrand.setBaseBrand(brandEntity);
                        providerBrand.setProvider(provider);
                        providerBrandsRepository.save(providerBrand);
                    });
        });

    }

    private BaseProviderCatalogEntity findOrCreateProviderCatalog(String externalId, final Object[] providerBrands) {
        return (BaseProviderCatalogEntity) Arrays.stream(providerBrands).toList().stream().filter(p ->((BaseProviderCatalogEntity) p).getExternalId().equals(externalId)).findFirst().orElse(new ProviderBrands());
    }


    private void downloadModelsCatalog(){

    }

    private void downloadTrimsCatalog(){

    }

}
