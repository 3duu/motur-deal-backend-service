package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.model.EndpointConfigEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public interface RequestService {
    Map<Object,Object> getMap(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity);

    List<Object> getList(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity);

    JsonNode getObject(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity);

    String getString(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity);

    Map<Object, Object> getAsMap(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity);

    Object execute(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity, EndpointConfigEntity autenticationEndpointConfigEntity) throws Exception;
}
