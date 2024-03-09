package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.model.EndpointConfig;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public interface RequestService {
    Map<Object,Object> getMap(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig);

    List<Object> getList(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig);

    JsonNode getObject(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig);

    String getString(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig);

    Map<Object, Object> getAsMap(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig);

    Object execute(ProviderEntity provider, EndpointConfig endpointConfig, EndpointConfig autenticationEndpointConfig);
}
