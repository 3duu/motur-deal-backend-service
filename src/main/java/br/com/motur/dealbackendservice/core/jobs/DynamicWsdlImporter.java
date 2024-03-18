package br.com.motur.dealbackendservice.core.jobs;

import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.common.ApiType;
import br.com.motur.dealbackendservice.core.model.common.SoapMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Importa WSDLs dinamicamente e armazena os nomes das operações e seus QNames
 */
@org.springframework.stereotype.Service
public class DynamicWsdlImporter {

    private final ProviderRepository providerRepository;
    private final Map<Integer, SoapMethod> operationsMap = new HashMap<>(); // HashMap to store operation names and their QNames
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public DynamicWsdlImporter(final ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
        providerRepository.findAllByApiType(ApiType.SOAP).forEach(providerEntity -> {
            try {
                importWsdl(providerEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public void importWsdl(final ProviderEntity provider) throws Exception {

        logger.info("Importando WSDL para o provedor: {}", provider.getName());

        // URL of the WSDL
        final URL wsdlLocation = new URL(provider.getUrl());

        // QName of the service: Namespace URI and local part
        final QName serviceName = new QName(provider.getUrl().replace("?wsdl", StringUtils.EMPTY), provider.getName());

        // Create a service instance
        Service service = Service.create(wsdlLocation, serviceName);

        // Iterate over all ports (bindings) and their operations
        for (Iterator<QName> it = service.getPorts(); it.hasNext(); ) {
            QName portName = it.next();
            // Assuming the portType interface is available
            // This part may require using additional libraries or custom logic to parse WSDL for operations
            // For demonstration, let's assume we have a method that returns operation names for a given port
            for (String operationName : getOperationNamesFromPort(portName)) {
                operationsMap.put(provider.getId(), new SoapMethod(provider, operationName, new QName(serviceName.getNamespaceURI(), operationName)));
            }
        }

        // Now, operationsMap contains operation names and their QNames
        // You can use this map as needed in your application
    }

    // Dummy method for demonstration purposes
    // In a real scenario, you would parse the WSDL to get operation names for the given port
    private static String[] getOperationNamesFromPort(QName portName) {
        // Implement logic to parse WSDL and retrieve operation names
        return new String[]{"Operation1", "Operation2"};
    }

    public SoapMethod getSoapMethod(final ProviderEntity provider) {
        return operationsMap.get(provider.getId());
    }
}

