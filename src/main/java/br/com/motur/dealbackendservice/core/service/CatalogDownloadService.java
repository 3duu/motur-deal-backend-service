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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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

    public void downloadCatalogData() {

        final List<ProviderEntity> providers = providerRepository.findAll();
        for (ProviderEntity provider : providers) {

            List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_BRANDS, provider);
            List<EndpointConfig> authEndpoint = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, provider);
            for (EndpointConfig endpointConfig : catalogEndpoints) {
                //requestRestService.execute(provider, endpointConfig, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);
                Map<String, Object> brands = (Map)requestRestService.execute(provider, endpointConfig,null);
                if(brands != null && !brands.isEmpty()){

                    if (endpointConfig.getReturnData() != null){

                        final List<BrandEntity> brandEntities = brandRepository.findAll();
                        final Object list = getValueFromNestedMap(brands, endpointConfig.getReturnData());
                        final List<ProviderBrands> providerBrands = providerBrandsRepository.findAll();

                        if (list instanceof List){
                            ((List) list).forEach(brand -> {
                                System.out.println(brand);
                            });
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

                }
            }
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

}
