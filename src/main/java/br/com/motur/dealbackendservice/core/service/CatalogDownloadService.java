package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.BrandEntity;
import br.com.motur.dealbackendservice.core.model.EndpointConfig;
import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.common.ApiType;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.Arrays;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    private Object getValueFromNestedMap(Map<String, Object> map, final String keys) {
        String[] splitKeys = keys.split("\\.");
        for (int i = 0; i < splitKeys.length - 1; i++) {
            map = (Map<String, Object>) map.get(splitKeys[i]);
            if (map == null) {
                return null;
            }
        }
        return map.get(splitKeys[splitKeys.length - 1]);
    }

    private void downloadBrandsCatalog(final ProviderEntity provider, final EndpointConfig authEndpoint) {
        final List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_BRANDS, provider);
        for (EndpointConfig endpointConfig : catalogEndpoints) {
            Map<String, Object> brands = (Map) requestRestService.execute(provider, endpointConfig, null);
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

    private void processAndSaveBrands(Map<String, Object> brands, EndpointConfig endpointConfig, final ProviderEntity provider) {
        if (endpointConfig.getReturnData() != null) {
            List<BrandEntity> brandEntities = brandRepository.findAll();
            Object list = getValueFromNestedMap(brands, endpointConfig.getReturnData());

            if (list instanceof List) {

                final List brandList = (List) list;
                if (!brandList.isEmpty() && brandList.get(0) instanceof Map){

                    List<Map<String, Object>> listMap = (List<Map<String, Object>>) list;

                    Map<String, Object> mapList = listMap.stream()
                            .flatMap(m -> m.entrySet().stream())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (v1, v2) -> v1
                            ));


                    brandList.forEach(brandMap -> processBrandMap(mapList, brandEntities, provider));
                }


            } else if (list instanceof Map) {
                processBrandMap((Map<String, Object>) list, brandEntities, provider);
            }
        }
    }

    private void processBrandMap(final Map<String, Object> brandMap, final List<BrandEntity> brandEntities, final ProviderEntity provider) {

        final List<ProviderBrands> providerBrands = providerBrandsRepository.findAllByProvider(provider);

        brandMap.forEach((key, value) -> {
            brandEntities.stream()
                    .filter(brandEntity -> brandEntity.getName().equalsIgnoreCase(key.toString()) ||
                            ArrayUtils.contains(brandEntity.getSynonymsArray(), key.toString()))
                    .findFirst()
                    .ifPresent(brandEntity -> {
                        String externalId = value.toString();
                        final ProviderBrands providerBrand = findOrCreateProviderBrand(externalId, providerBrands);
                        providerBrand.setName(key.toString());
                        providerBrand.setExternalId(externalId);
                        providerBrand.setBaseBrand(brandEntity);
                        providerBrandsRepository.save(providerBrand);
                    });
        });
    }

    private ProviderBrands findOrCreateProviderBrand(String externalId, final List<ProviderBrands> providerBrands) {
        return providerBrands.stream().filter(p -> p.getExternalId().equals(externalId)).findFirst().orElse(new ProviderBrands());
    }

    /**
     * Método responsável por extrair a lista de marcas dos dados retornados pelo endpoint
     * @param brandData
     * @param endpointConfig
     * @return
     */
    /*private List<Map<String, Object>> extractBrandList(final Map<String, Object> brandData, final EndpointConfig endpointConfig){

    }

    private List<Map<String, Object>> extractModelsList(Map<String, Object> modelsData, EndpointConfig endpointConfig) {

    }*/

    private void downloadModelsCatalog(){

    }

    private void downloadTrimsCatalog(){

    }

}
