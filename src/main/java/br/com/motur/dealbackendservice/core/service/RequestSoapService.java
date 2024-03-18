package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.EndpointConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.jobs.WsdlController;
import br.com.motur.dealbackendservice.core.model.EndpointConfigEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.soap.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
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

    private final WsdlController wsdlController;

    private static final String API_KEY = "X-API-Key";

    /**
     * Construtor da classe.
     * @param providerRepository Repository para ProviderEntity.
     * @param endpointConfigRepository Repository para EndpointConfigEntity.
     * @param authConfigRepository Repository para AuthConfigEntity.
     * @param objectMapper ObjectMapper para converting values.
     * @param wsdlController Controller para WSDL.
     * @see com.fasterxml.jackson.databind.ObjectMapper
     */
    public RequestSoapService(ProviderRepository providerRepository, EndpointConfigRepository endpointConfigRepository, AuthConfigRepository authConfigRepository, ObjectMapper objectMapper, WsdlController wsdlController) {
        this.providerRepository = providerRepository;
        this.endpointConfigRepository = endpointConfigRepository;
        this.authConfigRepository = authConfigRepository;
        this.objectMapper = objectMapper;
        this.wsdlController = wsdlController;
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
    public Object execute(final ProviderEntity provider, final EndpointConfigEntity endpointConfigEntity, final EndpointConfigEntity autenticationEndpointConfigEntity) {

        final Map<String, QName> operations = wsdlController.getOperationsMap(provider.getId());

        QName qName = operations.get(endpointConfigEntity.getUrl());

        SOAPMessage request = null;
        try {
            request = createRequest(qName.getLocalPart());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // The QName for the service and portType might need to be adjusted
        QName serviceName = new QName("http://example.com/", "YourServiceName");
        //QName portName = new QName("http://example.com/", portTypeName);

        // Create a service and dispatch
        javax.xml.ws.Service service = javax.xml.ws.Service.create(serviceName);
        //service.addPort(portName, javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING, wsdlUrl);
        Dispatch<SOAPMessage> dispatch = service.createDispatch(qName, SOAPMessage.class, javax.xml.ws.Service.Mode.MESSAGE);

        // Invoke the operation
        SOAPMessage response = dispatch.invoke(request);

        // Process the response
        processResponse(response);



        WebServiceTemplate webServiceTemplate = webServiceTemplate(endpointConfigEntity.getUrl(), endpointConfigEntity);
        // Assuming the payload is a SOAP request object
        return webServiceTemplate.marshalSendAndReceive(endpointConfigEntity.getPayload());
    }

    private SOAPMessage createRequest(String operationName) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();

        // This is a simplified example. You'll need to construct the request based on the operation's expected input
        QName operationQName = new QName("http://example.com/", operationName, "ns");
        SOAPElement operationElement = body.addChildElement(operationQName);
        // Add necessary elements to the operationElement based on the operation's requirements

        soapMessage.saveChanges();

        // Print the request message
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

    private void processResponse(SOAPMessage response) {
        // Implement response processing logic here
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
