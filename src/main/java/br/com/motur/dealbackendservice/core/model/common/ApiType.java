package br.com.motur.dealbackendservice.core.model.common;

import br.com.motur.dealbackendservice.core.service.RequestRestService;
import br.com.motur.dealbackendservice.core.service.RequestService;
import br.com.motur.dealbackendservice.core.service.RequestSoapService;
import lombok.Getter;

@Getter
public enum ApiType {

    REST("REST", RequestRestService.class),
    SOAP("SOAP", RequestSoapService.class),
    GRAPHQL("GraphQL", RequestService.class),
    GRPC("gRPC", RequestService.class),
    ODATA("OData", RequestService.class),
    JSON_RPC("JSON-RPC", RequestService.class),
    XML_RPC("XML-RPC", RequestService.class),
    OTHER("Outro", RequestService.class);

    private final String displayName;
    private final Class<? extends RequestService> requestService;

    ApiType(String displayName, Class<? extends RequestService> requestService) {
        this.displayName = displayName;
        this.requestService = requestService;
    }

}
