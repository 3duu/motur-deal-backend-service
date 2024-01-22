package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.BrandEntity;
import br.com.motur.dealbackendservice.core.model.EndpointConfig;
import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.common.ApiType;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
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
                if (provider.getApiType() == ApiType.REST){
                    //requestRestService.execute(provider, endpointConfig, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);
                    Map<String, Object> brands = (Map)requestRestService.execute(provider, endpointConfig,null);
                    if(brands != null && !brands.isEmpty()){

                        if (endpointConfig.getReturnData() != null){

                            final List<BrandEntity> brandEntities = brandRepository.findAll();
                            final Object list = getValueFromNestedMap(brands, endpointConfig.getReturnData());

                            if (list instanceof List){
                                ((List) list).forEach(brand -> {
                                    System.out.println(brand);
                                });
                            }
                            else if (list instanceof Map){

                                brandEntities.forEach(brandEntity -> {

                        //
                                    //brandEntity.setProvider(provider);
                                    brandRepository.save(brandEntity);
                                });

                                ((Map) list).forEach((key, value) -> {
                                    System.out.println(key);
                                    System.out.println(value);
                                });
                            }



                            final List<ProviderBrands> providerBrands = providerBrandsRepository.findAll();



                        }

                    }
                }
                else if (provider.getApiType() == ApiType.SOAP)
                    requestSoapService.execute(endpointConfig, authEndpoint.get(0));
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
