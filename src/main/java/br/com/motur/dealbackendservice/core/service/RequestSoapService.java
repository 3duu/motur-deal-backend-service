package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.ResponseProcessor;
import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.EndpointConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.jobs.WsdlController;
import br.com.motur.dealbackendservice.core.model.EndpointConfigEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;

import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.xml.soap.*;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import org.slf4j.Logger;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


import javax.xml.namespace.QName;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static jakarta.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;

/**
 * Essa classe é responsável por executar requisições SOAP
 */
@org.springframework.stereotype.Service
public class RequestSoapService implements RequestService {

    private final ProviderRepository providerRepository;
    private final EndpointConfigRepository endpointConfigRepository;

    private final AuthConfigRepository authConfigRepository;
    private final ObjectMapper objectMapper;

    private final WsdlController wsdlController;

    private final ResponseProcessor responseProcessor;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(RequestSoapService.class);

    /**
     * Construtor da classe.
     * @param providerRepository Repository para ProviderEntity.
     * @param endpointConfigRepository Repository para EndpointConfigEntity.
     * @param authConfigRepository Repository para AuthConfigEntity.
     * @param objectMapper ObjectMapper para converting values.
     * @param wsdlController Controller para WSDL.
     * @see com.fasterxml.jackson.databind.ObjectMapper
     */
    public RequestSoapService(ProviderRepository providerRepository, EndpointConfigRepository endpointConfigRepository,
                              AuthConfigRepository authConfigRepository, ObjectMapper objectMapper, WsdlController wsdlController,
                              Jackson2ObjectMapperBuilder mapperBuilder, ResponseProcessor responseProcessor) {
        this.providerRepository = providerRepository;
        this.endpointConfigRepository = endpointConfigRepository;
        this.authConfigRepository = authConfigRepository;
        this.objectMapper = objectMapper;
        this.wsdlController = wsdlController;
        this.responseProcessor = responseProcessor;
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @return Object.
     */
    @Override
    public Map<Object, Object> getMap(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity) {
        return (Map<Object, Object>) execute(provider, endpointConfigEntity);
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @return List<Object>.
     */
    @Override
    public List<Object> getList(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity) {
        return (List<Object>) execute(provider, endpointConfigEntity);
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @return JsonNode.
     */
    @Override
    public JsonNode getObject(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity) {
        return objectMapper.valueToTree(execute(provider, endpointConfigEntity));
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @return String.
     */
    @Override
    public String getString(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity) {
        return execute(provider, endpointConfigEntity).toString();
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @return Map<Object, Object>.
     */
    @Override
    public Map<Object, Object> getAsMap(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity) {
        return objectMapper.convertValue(execute(provider, endpointConfigEntity), Map.class);
    }

    /**
     * Método que executa a requisição SOAP.
     * @param provider ProviderEntity.
     * @param endpointConfigEntity EndpointConfigEntity.
     * @return List<Object>.
     */
    @Override
    public Object execute(final ProviderEntity provider, final EndpointConfigEntity endpointConfigEntity) {

        final Map<ResponseMapping.FieldMapping, Object> authData;
        if (endpointConfigEntity.getAuthEndpoint() != null){
            var ret = (Map<Object, Object>) execute(provider, endpointConfigEntity.getAuthEndpoint());

            if (!ret.isEmpty()){

                authData = new HashMap<>();
                Arrays.stream(ResponseMapping.FieldMapping.values()).forEach(field -> {
                    if (field != ResponseMapping.FieldMapping.RETURNS) {
                        final String value = responseProcessor.getStringFieldFromNestedMap(field, endpointConfigEntity.getResponseMapping(), ret, logger);
                        if (!value.isEmpty()){
                            //logger.info("Field: {} Value: {}", field, ret.get(field));
                            authData.put(field, value);
                        }

                    }
                });
            } else {
                authData = null;
            }
        } else {
            authData = null;
        }



        logger.info("Executing SOAP request for provider: {} from {} and endpoint: {}", provider.getName(), provider.getUrl(), endpointConfigEntity.getUrl());

        final Map<String, QName> operations = wsdlController.getOperationsMap(provider.getId());

        QName qName = operations.get(endpointConfigEntity.getUrl());

        if (authData != null){
            authData.forEach((key, value) ->
                    responseProcessor.updateEndpointConfigFields(endpointConfigEntity, key.getValue(), value.toString()));
        }

        SOAPMessage request = null;
        try {
            request = createRequest(qName, endpointConfigEntity.getPayload() != null ? objectMapper.convertValue(endpointConfigEntity.getPayload(), Map.class) : new HashMap<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // The QName for the service and portType might need to be adjusted

        // Create a service and dispatch
        Service service = Service.create(qName);
        service.addPort(qName, SOAP11HTTP_BINDING, provider.getUrl());
        Dispatch<SOAPMessage> dispatch = service.createDispatch(qName, SOAPMessage.class, Service.Mode.MESSAGE);

        // Invoke the operation
        final SOAPMessage response = dispatch.invoke(request);

        return parseSOAPMessageToHashMap(response);
    }

    private SOAPMessage createRequest(final QName operation, final Map<String, Object> requestData) throws Exception {

        final MessageFactory messageFactory = MessageFactory.newInstance();
        final SOAPMessage soapMessage = messageFactory.createMessage();
        soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, StandardCharsets.UTF_8.name());
        final SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();

        SOAPBody body = envelope.getBody();

        // Cria o elemento da operação
        final SOAPElement operationElement = body.addChildElement(operation);

        // Itera sobre o requestData e adiciona cada entrada como um elemento filho
        for (Map.Entry<String, Object> entry : requestData.entrySet()) {
            SOAPElement element = operationElement.addChildElement(entry.getKey());
            element.addTextNode(entry.getValue().toString());
        }

        soapMessage.saveChanges();

        return soapMessage;
    }

    private static Map<String, String> parseSOAPMessageToHashMap(SOAPMessage soapMessage) {
        Map<String, String> resultMap = new HashMap<>();

        SOAPBody soapBody = null;
        try {
            soapBody = soapMessage.getSOAPBody();
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }
        Iterator<?> iterator = soapBody.getChildElements();
        while (iterator.hasNext()) {
            Node node = (Node) iterator.next();
            if (node instanceof SOAPElement) {
                SOAPElement element = (SOAPElement) node;
                Iterator<?> childIterator = element.getChildElements();
                while (childIterator.hasNext()) {
                    Node childNode = (Node) childIterator.next();
                    if (childNode instanceof SOAPElement) {
                        SOAPElement childElement = (SOAPElement) childNode;
                        String key = childElement.getLocalName();
                        String value = childElement.getValue();
                        resultMap.put(key, value);
                    }
                }
            }
        }

        return resultMap;
    }


}
