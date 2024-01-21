package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.EndpointConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.EndpointConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class RequestSoapService {

    private final ProviderRepository providerRepository;
    private final EndpointConfigRepository endpointConfigRepository;
    private final ObjectMapper objectMapper;

    public RequestSoapService(ProviderRepository providerRepository, EndpointConfigRepository endpointConfigRepository, ObjectMapper objectMapper) {
        this.providerRepository = providerRepository;
        this.endpointConfigRepository = endpointConfigRepository;
        this.objectMapper = objectMapper;
    }

    public Object execute(EndpointConfig endpointConfig, EndpointConfig endpointAutheticationConfig) {
        return null;
    }
}
