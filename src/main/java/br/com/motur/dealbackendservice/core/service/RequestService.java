package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.model.EndpointConfigEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public interface RequestService {
    Map<Object,Object> getMap(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity);

    List<Object> getList(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity);

    JsonNode getObject(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity);

    String getString(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity);

    Map<Object, Object> getAsMap(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity);

    Object execute(ProviderEntity provider, EndpointConfigEntity endpointConfigEntity) throws Exception;
}
