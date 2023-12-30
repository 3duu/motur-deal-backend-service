package br.com.motur.dealbackendservice.core.model.common;

public enum ApiType {

    REST("REST"),
    SOAP("SOAP"),
    GRAPHQL("GraphQL"),
    GRPC("gRPC"),
    ODATA("OData"),
    JSON_RPC("JSON-RPC"),
    XML_RPC("XML-RPC"),
    OTHER("Outro");

    private final String displayName;

    ApiType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
