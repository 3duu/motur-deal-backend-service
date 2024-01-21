package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.EndpointConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.AuthConfigEntity;
import br.com.motur.dealbackendservice.core.model.EndpointConfig;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.common.AuthType;
import br.com.motur.dealbackendservice.core.model.common.EndpointMethod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class RequestRestService {

    private final ProviderRepository providerRepository;
    private final RestTemplate restTemplate;
    private final EndpointConfigRepository endpointConfigRepository;

    private final AuthConfigRepository authConfigRepository;
    private final ObjectMapper objectMapper;

    public RequestRestService(ProviderRepository providerRepository, RestTemplate restTemplate, EndpointConfigRepository endpointConfigRepository, AuthConfigRepository authConfigRepository, ObjectMapper objectMapper) {
        this.providerRepository = providerRepository;
        this.restTemplate = restTemplate;
        this.endpointConfigRepository = endpointConfigRepository;
        this.authConfigRepository = authConfigRepository;
        this.objectMapper = objectMapper;
    }

    /**
     *
     * @param provider Provedor
     * @param endpointConfig Configuração do endpoint
     * @param autenticationEndpointConfig (opcional) Fazer requisição de autenticação antes de executar a requisição principal
     * @return
     */
    public Object execute(final ProviderEntity provider, final EndpointConfig endpointConfig, final EndpointConfig autenticationEndpointConfig) {

        if (autenticationEndpointConfig != null){

            AuthConfigEntity authConfig = authConfigRepository.findByProviderId(provider.getId());

            if (authConfig != null)
                execute(endpointConfig.getUrl(), HttpMethod.valueOf(autenticationEndpointConfig.getMethod().name()), autenticationEndpointConfig.getAdditionalParams(), autenticationEndpointConfig.getHeaders(), autenticationEndpointConfig.getPayload(), authConfig);
        }

        return execute(endpointConfig.getUrl(), HttpMethod.valueOf(endpointConfig.getMethod().name()), endpointConfig.getAdditionalParams(), endpointConfig.getHeaders(), endpointConfig.getPayload(), null);
    }

    private Object execute(String url, final HttpMethod httpMethod, final JsonNode additionalParams, final JsonNode headers, final JsonNode payload, final AuthConfigEntity authConfig) {

        if (authConfig != null){

            if (AuthType.API_KEY == authConfig.getAuthType()){

            }
            else if (AuthType.QUERY_PARAMS == authConfig.getAuthType()){

                //if ()

                if (!url.endsWith("?")){
                    url = url.concat("?");
                }

                Iterator<Map.Entry<String, JsonNode>> fields = authConfig.getDetails().fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    url = url.concat(field.getKey()).concat("=").concat(field.getValue().asText()).concat("&");
                }

            }
        }

        Map<String, Object> response = null;
        if (httpMethod == null || httpMethod.equals(HttpMethod.GET)) {

            if (additionalParams != null){
                if (!url.endsWith("?")){
                    url = url.concat("?");
                }


                for (JsonNode additionalParam : additionalParams) {
                    url = url.concat(additionalParam.get("key").asText()).concat("=").concat(additionalParam.get("value").asText()).concat("&");
                }
            }
            RequestEntity requestEntity = RequestEntity.get(url).build();

            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class).getBody();

        } else if (httpMethod.equals(EndpointMethod.POST)) {

            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAll(headers != null ? objectMapper.convertValue(headers, Map.class) : new HashMap());

            RequestEntity requestEntity = RequestEntity.post(url)
                    //.contentType(contentType)
                    .headers(httpHeaders)
                    //.accept(MediaType.APPLICATION_JSON)
                    .body(payload);
            //.build();


            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class).getBody();
        } else {
            throw new RuntimeException("Método não suportado: " + httpMethod);
        }

        return response;
    }


}
