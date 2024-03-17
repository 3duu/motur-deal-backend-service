package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.EndpointConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.EndpointConfigEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
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

    /**
     * Construtor da classe.
     * @param providerRepository Repository para ProviderEntity.
     * @param endpointConfigRepository Repository para EndpointConfigEntity.
     * @param authConfigRepository Repository para AuthConfigEntity.
     * @param objectMapper ObjectMapper para converting values.
     */
    public RequestSoapService(ProviderRepository providerRepository, EndpointConfigRepository endpointConfigRepository, AuthConfigRepository authConfigRepository, ObjectMapper objectMapper) {
        this.providerRepository = providerRepository;
        this.endpointConfigRepository = endpointConfigRepository;
        this.authConfigRepository = authConfigRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @param autenticationEndpointConfigEntity EndpointConfigEntity.
     * @return Object.
     */
    @Override
    public Map<Object, Object> getMap(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity) {
        return (Map<Object, Object>) execute(provider, endpointConfigEntity, autenticationEndpointConfigEntity);
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @param autenticationEndpointConfigEntity EndpointConfigEntity.
     * @return List<Object>.
     */
    @Override
    public List<Object> getList(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity) {
        return (List<Object>) execute(provider, endpointConfigEntity, autenticationEndpointConfigEntity);
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @param autenticationEndpointConfigEntity EndpointConfigEntity.
     * @return JsonNode.
     */
    @Override
    public JsonNode getObject(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity) {
        return objectMapper.valueToTree(execute(provider, endpointConfigEntity, autenticationEndpointConfigEntity));
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @param autenticationEndpointConfigEntity EndpointConfigEntity.
     * @return String.
     */
    @Override
    public String getString(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity) {
        return execute(provider, endpointConfigEntity, autenticationEndpointConfigEntity).toString();
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @param autenticationEndpointConfigEntity EndpointConfigEntity.
     * @return Map<Object, Object>.
     */
    @Override
    public Map<Object, Object> getAsMap(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity) {
        return objectMapper.convertValue(execute(provider, endpointConfigEntity, autenticationEndpointConfigEntity), Map.class);
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @param autenticationEndpointConfigEntity EndpointConfigEntity.
     * @return List<Object>.
     */
    @Override
    public Object execute(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity) {
        WebServiceTemplate webServiceTemplate = webServiceTemplate(endpointConfigEntity.getUrl(), endpointConfigEntity.getUrl());
        // Assuming the payload is a SOAP request object
        return webServiceTemplate.marshalSendAndReceive(endpointConfigEntity.getPayload());
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
