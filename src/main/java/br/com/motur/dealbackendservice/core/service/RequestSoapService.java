package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.EndpointConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.jobs.WsdlController;
import br.com.motur.dealbackendservice.core.model.EndpointConfigEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sun.xml.messaging.saaj.soap.ver1_1.Message1_1Impl;
import jakarta.xml.soap.*;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    private final XmlMapper xmlMapper;

    /**
     * Construtor da classe.
     * @param providerRepository Repository para ProviderEntity.
     * @param endpointConfigRepository Repository para EndpointConfigEntity.
     * @param authConfigRepository Repository para AuthConfigEntity.
     * @param objectMapper ObjectMapper para converting values.
     * @param wsdlController Controller para WSDL.
     * @see com.fasterxml.jackson.databind.ObjectMapper
     */
    public RequestSoapService(ProviderRepository providerRepository, EndpointConfigRepository endpointConfigRepository, AuthConfigRepository authConfigRepository, ObjectMapper objectMapper, WsdlController wsdlController, Jackson2ObjectMapperBuilder mapperBuilder) {
        this.providerRepository = providerRepository;
        this.endpointConfigRepository = endpointConfigRepository;
        this.authConfigRepository = authConfigRepository;
        this.objectMapper = objectMapper;
        this.wsdlController = wsdlController;
        this.xmlMapper = mapperBuilder.createXmlMapper(true).build();
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

        final Map<Object, Object> authData;
        if (endpointConfigEntity.getAuthEndpoint() != null){
            authData = getAsMap(provider, endpointConfigEntity.getAuthEndpoint());
        }

        final Map<String, QName> operations = wsdlController.getOperationsMap(provider.getId());

        QName qName = operations.get(endpointConfigEntity.getUrl());

        SOAPMessage request = null;
        try {
            request = createRequest(provider, qName, endpointConfigEntity.getPayload());
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

        return getResponse(response);
    }

    private SOAPMessage createRequest(final ProviderEntity provider, final QName operation, final JsonNode payload) throws Exception {

        final Map<String, Object> requestData = payload != null ? objectMapper.convertValue(payload, Map.class) : new HashMap<>();
        final MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();

        SOAPBody body = envelope.getBody();

        // Cria o elemento da operação
        final SOAPElement operationElement = body.addChildElement(operation);

        // Itera sobre o requestData e adiciona cada entrada como um elemento filho
        for (Map.Entry<String, Object> entry : requestData.entrySet()) {
            // Cria um novo elemento para cada entrada no Map
            SOAPElement element = operationElement.addChildElement(entry.getKey());
            // Define o valor do elemento. Assumindo que os valores são Strings ou tipos que possam ser corretamente representados como Strings.
            // Para casos mais complexos, você pode precisar de lógica adicional para tratar tipos específicos ou estruturas aninhadas.
            element.addTextNode(entry.getValue().toString());
        }

        soapMessage.saveChanges();

        return soapMessage;
    }

    /*private Map<String, Object> getResponse(SOAPMessage response) {
        try {
            return (Map<String, Object>) xmlMapper.readTree(response.toString());
        } catch (Exception e) {
            return null;
        }
    }*/

    public HashMap<String, Object> getResponse(SOAPMessage soapMessage) {
        HashMap<String, Object> resultMap = new HashMap<>();
        SOAPBody body = null;
        try {
            body = soapMessage.getSOAPBody();
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }

        // Extrai todos os elementos do corpo da mensagem SOAP
        Iterator<?> iterator = body.getChildElements();
        while (iterator.hasNext()) {
            Object nextElement = iterator.next();
            if (nextElement instanceof SOAPElement) {
                SOAPElement soapElement = (SOAPElement) nextElement;
                // Chama o método recursivo para processar o elemento e seus filhos
                processSOAPElement(soapElement, resultMap);
            }
        }

        return resultMap;
    }

    private void processSOAPElement(SOAPElement element, HashMap<String, Object> map) {
        // Assume-se que os elementos têm nomes únicos no nível em que se encontram
        // Se houver múltiplos elementos com o mesmo nome (por exemplo, em uma lista), este código precisará ser ajustado
        QName elementQName = element.getElementQName();
        String elementName = elementQName.getLocalPart();

        // Verifica se o elemento tem filhos
        Iterator<?> iterator = element.getChildElements();
        if (!iterator.hasNext()) {
            // Se não tem filhos, trata como um valor de texto
            map.put(elementName, element.getValue());
        } else {
            // Se tem filhos, cria um novo HashMap para esses filhos e os processa recursivamente
            HashMap<String, Object> childMap = new HashMap<>();
            while (iterator.hasNext()) {
                Object child = iterator.next();
                if (child instanceof SOAPElement) {
                    processSOAPElement((SOAPElement) child, childMap);
                }
            }
            map.put(elementName, childMap);
        }
    }

    private Jaxb2Marshaller marshaller(String contextPath) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(contextPath);
        return marshaller;
    }

    private WebServiceTemplate webServiceTemplate(String contextPath, EndpointConfigEntity endpointConfigEntity) {

        Jaxb2Marshaller marshaller = marshaller(contextPath);
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        // URL do serviço SOAP
        webServiceTemplate.setDefaultUri(endpointConfigEntity.getUrl());
        return webServiceTemplate;
    }
}
