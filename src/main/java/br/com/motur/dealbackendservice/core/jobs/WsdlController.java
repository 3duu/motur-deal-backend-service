package br.com.motur.dealbackendservice.core.jobs;

import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.common.ApiType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import javax.xml.namespace.QName;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WsdlController {

    private final ProviderRepository providerRepository;
    private final Map<Integer, Map<String, QName>> operationsMap = new HashMap<>(); // HashMap to store operation names and their QNames
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public WsdlController(final ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
        providerRepository.findAllByApiType(ApiType.SOAP).forEach(providerEntity -> {
            try {
                loadAndParseWsdl(providerEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        /*providerRepository.findAllByApiType(ApiType.SOAP).forEach(providerEntity -> {
            try {
                loadAndParseWsdl(providerEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });*/
    }

    public Map<String, QName> loadAndParseWsdl(final ProviderEntity provider) throws Exception {

        logger.info("Importando WSDL para o provedor: {}", provider.getName());

        final Map<String, QName> returnMap = new HashMap<>();
        try {
            // URL of the WSDL
            final URL wsdlUrl = new URL(provider.getUrl());

            // Use WSDL4J to read and parse the WSDL
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();

            // Disable verbose and set feature to not import other WSDLs
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setFeature("javax.wsdl.importDocuments", false);

            Definition wsdlDefinition = reader.readWSDL(wsdlUrl.toString());

            // Iterate over all port types (interfaces)
            Map portTypes = wsdlDefinition.getPortTypes();
            for (Object portTypeObj : portTypes.values()) {
                PortType portType = (PortType) portTypeObj;
                List<Operation> operations = portType.getOperations();

                // Iterate over operations and store them in the map
                for (Operation operation : operations) {
                    QName operationQName = new QName(portType.getQName().getNamespaceURI(), operation.getName());
                    returnMap.put(operation.getName(), operationQName);
                }
            }

            //Map<String, List<String>> operationsMap = new HashMap<>();

            // Iterate over all port types to get operations
            /*wsdlDefinition.getAllPortTypes().forEach((key, value) -> {
                PortType portType = (PortType) value;
                List<String> operations = portType.getOperations().stream()
                        .map(Operation::getName)
                        .collect(Collectors.toList());
                operationsMap.put(portType.getQName().toString(), operations);
            });*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        operationsMap.put(provider.getId(), returnMap);

        return returnMap;
    }

    public Map<String, QName> getOperationsMap(final Integer providerId) {
        return operationsMap.get(providerId);
    }
}
