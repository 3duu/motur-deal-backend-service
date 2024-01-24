package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.ApiType;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import br.com.motur.dealbackendservice.core.model.common.ReturnMapping;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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

    public CatalogDownloadService(ProviderRepository providerRepository, RestTemplate restTemplate, EndpointConfigRepository endpointConfigRepository, RequestRestService requestRestService, RequestSoapService requestSoapService, ProviderBrandsRepository providerBrandsRepository, ProviderModelsRepository providerModelsRepository, ProviderTrimsRepository providerTrimsRepository, BrandRepository brandRepository) {
        this.providerRepository = providerRepository;
        this.restTemplate = restTemplate;
        this.endpointConfigRepository = endpointConfigRepository;
        this.requestRestService = requestRestService;
        this.requestSoapService = requestSoapService;
        this.providerBrandsRepository = providerBrandsRepository;
        this.providerModelsRepository = providerModelsRepository;
        this.providerTrimsRepository = providerTrimsRepository;
        this.brandRepository = brandRepository;
    }

    //@EventListener(ApplicationReadyEvent.class)
    public void downloadCatalogData() {

        final List<ProviderEntity> providers = providerRepository.findAll();
        for (ProviderEntity provider : providers) {

            final List<EndpointConfig> authEndpoint = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, provider);
            downloadBrandsCatalog(provider, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);

        }
    }

    private Object getValueFromNestedMap(final EndpointConfig endpoint, Map<Object, Object> origin, final List<BrandEntity> destination) {

        if (endpoint == null || endpoint.getReturnMapping() == null) {
            return null;
        }

        endpoint.getReturnMapping().getFieldMappings().forEach(config -> {

            final String[] path = config.getOriginPath().split("\\.");
            if (path.length > 1) {

                final Map<Object, Object> nestedMap = (Map<Object, Object>) origin.get(path[0]);
                if (nestedMap != null) {
                    //origin = nestedMap;
                }
            }
        });

        return origin;
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

    /*private void processAndSaveBrands(Map<String, Object> brands, final EndpointConfig endpointConfig, final ProviderEntity provider) {

        if (brands != null && !brands.isEmpty()){

            final List<BrandEntity> brandEntities = brandRepository.findAll();
            final Object list = getValueFromNestedMap(brands, endpointConfig.getReturnData());
            final List<ProviderBrands> providerBrands = providerBrandsRepository.findAllByProvider(provider);

            if (list instanceof List){

                final List brandList = (List) list;
                if (!brandList.isEmpty() && brandList.get(0) instanceof Map){

                    brandList.forEach(brand -> {

                        final Map<Object, Object> data = (Map<Object, Object>) brand;
                        if (!data.isEmpty()) {

                            if (data.keySet().toArray()[0] instanceof String) {

                                data.forEach((key, value) -> {

                                    brandEntities.stream().filter(brandEntity -> brandEntity.getName().trim().toLowerCase().equals(key.toString().trim().toLowerCase())
                                            || ArrayUtils.contains(brandEntity.getSynonymsArray(), key.toString().trim().toLowerCase())).findFirst().ifPresent(brandEntity -> {

                                        final String externalId = value.toString().trim();
                                        final ProviderBrands providerBrand = providerBrands.stream().filter(p -> p.getExternalId().equals(externalId)).findFirst().orElse(new ProviderBrands());
                                        providerBrand.setProvider(provider);
                                        providerBrand.setName(key.toString().trim());
                                        providerBrand.setExternalId(externalId);
                                        providerBrand.setBaseBrand(brandEntity);
                                        providerBrandsRepository.save(providerBrand);
                                    });

                                });
                            } else {
                                data.forEach((key, value) -> {

                                    brandEntities.stream().filter(brandEntity -> brandEntity.getName().trim().toLowerCase().equals(key.toString().trim().toLowerCase())
                                            || ArrayUtils.contains(brandEntity.getSynonymsArray(), key.toString().trim().toLowerCase())).findFirst().ifPresent(brandEntity -> {

                                        final String externalId = key.toString().trim();
                                        final ProviderBrands providerBrand = providerBrands.stream().filter(p -> p.getExternalId().equals(externalId)).findFirst().orElse(new ProviderBrands());
                                        providerBrand.setProvider(provider);
                                        providerBrand.setName(value.toString().trim());
                                        providerBrand.setExternalId(externalId);
                                        providerBrand.setBaseBrand(brandEntity);
                                        providerBrandsRepository.save(providerBrand);
                                    });
                                });
                            }
                        }
                    });
                }

            }
            else if (list instanceof Map){

                final Map<Object, Object> data = (Map<Object, Object>) list;
                if (!data.isEmpty()){

                    if (data.keySet().toArray()[0] instanceof String){

                        data.forEach((key, value) -> {

                            brandEntities.stream().filter(brandEntity -> brandEntity.getName().trim().toLowerCase().equals(key.toString().trim().toLowerCase())
                                    || ArrayUtils.contains(brandEntity.getSynonymsArray(), key.toString().trim().toLowerCase())).findFirst().ifPresent(brandEntity -> {

                                final String externalId = value.toString().trim();
                                final ProviderBrands providerBrand = providerBrands.stream().filter(p -> p.getExternalId().equals(externalId)).findFirst().orElse(new ProviderBrands());
                                providerBrand.setProvider(provider);
                                providerBrand.setName(key.toString().trim());
                                providerBrand.setExternalId(externalId);
                                providerBrand.setBaseBrand(brandEntity);
                                providerBrandsRepository.save(providerBrand);
                            });

                        });
                    }
                    else {
                        data.forEach((key, value) -> {

                            brandEntities.stream().filter(brandEntity -> brandEntity.getName().trim().toLowerCase().equals(key.toString().trim().toLowerCase())
                                    || ArrayUtils.contains(brandEntity.getSynonymsArray(), key.toString().trim().toLowerCase())).findFirst().ifPresent(brandEntity -> {

                                final String externalId = key.toString().trim();
                                final ProviderBrands providerBrand = providerBrands.stream().filter(p -> p.getExternalId().equals(externalId)).findFirst().orElse(new ProviderBrands());
                                providerBrand.setProvider(provider);
                                providerBrand.setName(value.toString().trim());
                                providerBrand.setExternalId(externalId);
                                providerBrand.setBaseBrand(brandEntity);
                                providerBrandsRepository.save(providerBrand);
                            });


                        });
                    }

                }

            }

        }
    }*/

    private void processAndSaveBrands(final Map<Object, Object> brands, final EndpointConfig endpointConfig, final ProviderEntity provider) {

        final List<BrandEntity> brandEntities = brandRepository.findAll();
        final Object list = getValueFromNestedMap(endpointConfig, brands, brandEntities);

        if (list instanceof List) {

            final List brandList = (List) list;
            if (!brandList.isEmpty() && brandList.get(0) instanceof Map){

                final List<Map<Object, Object>> listMap = (List<Map<Object, Object>>) list;

                final Map<Object, Object> mapList = listMap.stream()
                        .flatMap(m -> m.entrySet().stream())
                        .collect(Collectors.toMap(
                                entry -> entry.getKey(),  // replaced Map.Entry::getKey with lambda
                                entry -> entry.getValue(),  // replaced Map.Entry::getValue with lambda
                                (v1, v2) -> v1
                        ));


                brandList.forEach(brandMap -> processBrandMap(mapList, brandEntities, provider));
            }


        } else if (list instanceof Map) {
            processBrandMap((Map<Object, Object>) list, brandEntities, provider);
        }

    }

    private void processBrandMap(final Map<Object, Object> brandMap, final List<BrandEntity> brandEntities, final ProviderEntity provider) {

        final List<ProviderBrands> providerBrands = providerBrandsRepository.findAllByProvider(provider);

        var keyset = brandMap.keySet().toArray();
        boolean keyIsString = keyset.length > 0 && keyset[0] instanceof String;

        brandMap.forEach((key, value) -> {
            brandEntities.stream()
                    .filter(brandEntity -> brandEntity.getName().equalsIgnoreCase(keyIsString ? key.toString() : value.toString()) ||
                            ArrayUtils.contains(brandEntity.getSynonymsArray(), keyIsString ? key.toString() : value.toString()))
                    .findFirst()
                    .ifPresent(brandEntity -> {

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
