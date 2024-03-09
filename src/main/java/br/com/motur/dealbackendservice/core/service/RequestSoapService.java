package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.EndpointConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.EndpointConfig;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.List;
import java.util.Map;

/**
 * Essa classe é responsável por executar requisições SOAP
 */
@Service
public class RequestSoapService implements RequestService {

    private final ProviderRepository providerRepository;
    private final EndpointConfigRepository endpointConfigRepository;

    private final AuthConfigRepository authConfigRepository;
    private final ObjectMapper objectMapper;

    private static final String API_KEY = "X-API-Key";

    public RequestSoapService(ProviderRepository providerRepository, EndpointConfigRepository endpointConfigRepository, AuthConfigRepository authConfigRepository, ObjectMapper objectMapper) {
        this.providerRepository = providerRepository;
        this.endpointConfigRepository = endpointConfigRepository;
        this.authConfigRepository = authConfigRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<Object, Object> getMap(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig) {
        return (Map<Object, Object>) execute(provider, endpointConfig, autenticationEndpointConfig);
    }

    @Override
    public List<Object> getList(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig) {
        return (List<Object>) execute(provider, endpointConfig, autenticationEndpointConfig);
    }

    @Override
    public JsonNode getObject(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig) {
        return objectMapper.valueToTree(execute(provider, endpointConfig, autenticationEndpointConfig));
    }

    @Override
    public String getString(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig) {
        return execute(provider, endpointConfig, autenticationEndpointConfig).toString();
    }

    @Override
    public Map<Object, Object> getAsMap(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig) {
        return objectMapper.convertValue(execute(provider, endpointConfig, autenticationEndpointConfig), Map.class);
    }

    @Override
    public Object execute(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig) {
        WebServiceTemplate webServiceTemplate = webServiceTemplate(endpointConfig.getUrl(), endpointConfig.getUrl());
        // Assuming the payload is a SOAP request object
        return webServiceTemplate.marshalSendAndReceive(endpointConfig.getPayload());
    }

    private Jaxb2Marshaller marshaller(String contextPath) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(contextPath);
        return marshaller;
    }

    private WebServiceTemplate webServiceTemplate(String contextPath, String defaultUri) {

        Jaxb2Marshaller marshaller = marshaller(contextPath);
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        // URL do serviço SOAP
        webServiceTemplate.setDefaultUri(defaultUri);
        return webServiceTemplate;
    }
}
