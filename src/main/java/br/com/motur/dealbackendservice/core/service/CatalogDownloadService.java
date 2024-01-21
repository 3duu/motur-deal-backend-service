package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.EndpointConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.EndpointConfig;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.common.ApiType;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CatalogDownloadService {

    private final ProviderRepository providerRepository;
    private final RestTemplate restTemplate;
    private final EndpointConfigRepository endpointConfigRepository;

    private final RequestRestService requestRestService;
    private final RequestSoapService requestSoapService;

    public CatalogDownloadService(ProviderRepository providerRepository, RestTemplate restTemplate, EndpointConfigRepository endpointConfigRepository, RequestRestService requestRestService, RequestSoapService requestSoapService) {
        this.providerRepository = providerRepository;
        this.restTemplate = restTemplate;
        this.endpointConfigRepository = endpointConfigRepository;
        this.requestRestService = requestRestService;
        this.requestSoapService = requestSoapService;
    }

    public void downloadCatalogData() {

        final List<ProviderEntity> providers = providerRepository.findAll();
        for (ProviderEntity provider : providers) {

            List<EndpointConfig> catalogEndpoints = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_BRANDS, provider);
            List<EndpointConfig> authEndpoint = endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, provider);
            for (EndpointConfig endpointConfig : catalogEndpoints) {
                if (provider.getApiType() == ApiType.REST)
                    requestRestService.execute(provider, endpointConfig, !authEndpoint.isEmpty() ? authEndpoint.get(0) : null);
                else if (provider.getApiType() == ApiType.SOAP)
                    requestSoapService.execute(endpointConfig, authEndpoint.get(0));
            }
        }


    }

    private void downloadCatalogForProvider(final ProviderEntity provider) {
        // Exemplo para baixar marcas

        try{
            if (provider.getApiType() == ApiType.REST) {
                //downloadCatalogForProviderRest(provider);
            } else if (provider.getApiType() == ApiType.SOAP) {
                //downloadCatalogForProviderSoap(provider);
            }
        }
        catch(Exception e){
            System.out.println("Erro ao baixar catálogo do provedor " + provider.getName());
        }

    }




}
