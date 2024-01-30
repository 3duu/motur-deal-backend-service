package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.EndpointConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.AuthConfigEntity;
import br.com.motur.dealbackendservice.core.model.EndpointConfig;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.common.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Essa classe é responsável por executar requisições REST
 */
@Service
public class RequestRestService {

    private final ProviderRepository providerRepository;
    private final RestTemplate restTemplate;
    private final EndpointConfigRepository endpointConfigRepository;

    private final AuthConfigRepository authConfigRepository;
    private final ObjectMapper objectMapper;

    private static final String API_KEY = "X-API-Key";

    public RequestRestService(ProviderRepository providerRepository, RestTemplate restTemplate, EndpointConfigRepository endpointConfigRepository, AuthConfigRepository authConfigRepository, ObjectMapper objectMapper) {
        this.providerRepository = providerRepository;
        this.restTemplate = restTemplate;
        this.endpointConfigRepository = endpointConfigRepository;
        this.authConfigRepository = authConfigRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Executa uma requisição REST retornando um HashMap
     * @param provider Provedor
     * @param endpointConfig Configuração do endpoint
     * @param autenticationEndpointConfig (opcional) Fazer requisição de autenticação antes de executar a requisição principal
     * @return
     */
    public Map<Object,Object> getMap(final ProviderEntity provider, final EndpointConfig endpointConfig, final EndpointConfig autenticationEndpointConfig) {

        if (endpointConfig.getResponseMapping().getFieldMappings() != null && endpointConfig.getResponseMapping().getFieldMappings().size() > 0){

            if (endpointConfig.getResponseMapping().getFieldMappings().get(0).getOriginDatatype().equals(DataType.MAP)){
                return (Map)execute(provider, endpointConfig, autenticationEndpointConfig);
            }
            else
                return null;
        }

        return (Map)execute(provider, endpointConfig, autenticationEndpointConfig);
    }

    /**
     * Executa uma requisição REST retornando uma lista
     * @param provider Provedor
     * @param endpointConfig Configuração do endpoint
     * @param autenticationEndpointConfig (opcional) Fazer requisição de autenticação antes de executar a requisição principal
     * @return
     */
    public List<Object> getList(final ProviderEntity provider, final EndpointConfig endpointConfig, final EndpointConfig autenticationEndpointConfig) {

        if (endpointConfig.getResponseMapping().getFieldMappings() != null && endpointConfig.getResponseMapping().getFieldMappings().size() > 0){

            if (endpointConfig.getResponseMapping().getFieldMappings().get(0).getOriginDatatype().equals(DataType.LIST)){
                return (List)execute(provider, endpointConfig, autenticationEndpointConfig);
            }
            else
                return null;
        }

        return (List)execute(provider, endpointConfig, autenticationEndpointConfig);
    }

    /**
     * Executa uma requisição REST retornando um JsonNode
     * @param provider Provedor
     * @param endpointConfig Configuração do endpoint
     * @param autenticationEndpointConfig (opcional) Fazer requisição de autenticação antes de executar a requisição principal
     * @return
     */
    public JsonNode getObject(final ProviderEntity provider, final EndpointConfig endpointConfig, final EndpointConfig autenticationEndpointConfig) {

        if (endpointConfig.getResponseMapping().getFieldMappings() != null && endpointConfig.getResponseMapping().getFieldMappings().size() > 0){

            if (endpointConfig.getResponseMapping().getFieldMappings().get(0).getOriginDatatype().equals(DataType.JSON)){
                return (JsonNode)execute(provider, endpointConfig, autenticationEndpointConfig);
            }
            else
                return null;
        }

        return (JsonNode)execute(provider, endpointConfig, autenticationEndpointConfig);
    }

    /**
     * Executa uma requisição REST retornando uma String
     * @param provider Provedor
     * @param endpointConfig Configuração do endpoint
     * @param autenticationEndpointConfig (opcional) Fazer requisição de autenticação antes de executar a requisição principal
     * @return
     */
    public String getString(final ProviderEntity provider, final EndpointConfig endpointConfig, final EndpointConfig autenticationEndpointConfig) {

        if (endpointConfig.getResponseMapping().getFieldMappings() != null && endpointConfig.getResponseMapping().getFieldMappings().size() > 0){

            if (endpointConfig.getResponseMapping().getFieldMappings().get(0).getOriginDatatype().equals(DataType.STRING)){
                return (String)execute(provider, endpointConfig, autenticationEndpointConfig);
            }
            else
                return null;
        }

        return (String)execute(provider, endpointConfig, autenticationEndpointConfig);
    }

    public Map<Object, Object> getAsMap(final ProviderEntity provider, final EndpointConfig endpointConfig, final EndpointConfig autenticationEndpointConfig){

        if (endpointConfig.getResponseMapping().getFieldMappings() != null && endpointConfig.getResponseMapping().getFieldMappings().size() > 0){

                final DataType dataType = endpointConfig.getResponseMapping().getFieldMappings().get(0).getOriginDatatype();

                switch (dataType) {
                    case MAP:
                        return getMap(provider, endpointConfig, autenticationEndpointConfig);
                    case LIST:
                        var list = getList(provider, endpointConfig, autenticationEndpointConfig);
                        return IntStream.range(0, list.size())
                            .boxed()
                            .collect(Collectors.toMap(i -> i, list::get));
                    case JSON:
                        return null;
                    case STRING:
                        return null;
                }
        }
        return getMap(provider, endpointConfig, autenticationEndpointConfig);
    }

    /**
     * Executa uma requisição REST
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

    private Object execute(String url, final HttpMethod httpMethod, JsonNode additionalParams, final JsonNode headers, final JsonNode payload, final AuthConfigEntity authConfig) {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(headers != null ? objectMapper.convertValue(headers, Map.class) : new HashMap());

        if (authConfig != null){

            if (AuthType.API_KEY == authConfig.getAuthType()){
                ApiKeyAuthConfig apiKeyAuthConfig = objectMapper.convertValue(authConfig.getDetails(), ApiKeyAuthConfig.class);
                httpHeaders.set(API_KEY, apiKeyAuthConfig.getApiKey());
            }
            else if (AuthType.QUERY_PARAMS == authConfig.getAuthType()){

                if (!url.endsWith("?")){
                    url = url.concat("?");
                }

                Iterator<Map.Entry<String, JsonNode>> fields = authConfig.getDetails().fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    url = url.concat(field.getKey()).concat("=").concat(field.getValue().asText()).concat("&");
                }

            }
            else if (AuthType.CUSTOM == authConfig.getAuthType()){
                Map<String, Object> customAuthConfig = objectMapper.convertValue(authConfig.getDetails(), Map.class);
            }
            else if (AuthType.BASIC == authConfig.getAuthType()){
                BasicAuthConfig basicAuthConfig = objectMapper.convertValue(authConfig.getDetails(), BasicAuthConfig.class);
            }
            else if (AuthType.OAUTH2 == authConfig.getAuthType()){
                OAuth2AuthConfig oAuth2AuthConfig = objectMapper.convertValue(authConfig.getDetails(), OAuth2AuthConfig.class);
            }
            else if (AuthType.BEARER_TOKEN == authConfig.getAuthType()){
                BearerTokenAuthConfig bearerTokenAuthConfig = objectMapper.convertValue(authConfig.getDetails(), BearerTokenAuthConfig.class);
            }
            else if (AuthType.DIGEST == authConfig.getAuthType()){
                DigestAuthConfig digestAuthConfig = objectMapper.convertValue(authConfig.getDetails(), DigestAuthConfig.class);
            }
            else if (AuthType.JWT == authConfig.getAuthType()){
                //JwtAuthConfig jwtAuthConfig = objectMapper.convertValue(authConfig.getDetails(), JwtAuthConfig.class);
            }
            else if (AuthType.SAML == authConfig.getAuthType()){
                //SamlAuthConfig samlAuthConfig = objectMapper.convertValue(authConfig.getDetails(), SamlAuthConfig.class);
            }
            else if (AuthType.OPENID_CONNECT == authConfig.getAuthType()){
                //OpenIdConnectAuthConfig openIdConnectAuthConfig = objectMapper.convertValue(authConfig.getDetails(), OpenIdConnectAuthConfig.class);
            }
            else if (AuthType.NTLM == authConfig.getAuthType()){
                //NtlmAuthConfig ntlmAuthConfig = objectMapper.convertValue(authConfig.getDetails(), NtlmAuthConfig.class);
            }
        }

        Object response = null;
        if (httpMethod == null || httpMethod.equals(HttpMethod.GET)) {

            if (additionalParams != null){
                if (!url.endsWith("?")){
                    url = url.concat("?");
                }

                Iterator<Map.Entry<String, JsonNode>> fields = additionalParams.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    url = url.concat(field.getKey()).concat("=").concat(field.getValue().asText()).concat("&");
                }
            }
            RequestEntity requestEntity = RequestEntity.get(url).headers(httpHeaders).build();

            response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class).getBody();

        } else if (httpMethod.equals(HttpMethod.POST)) {

            RequestEntity requestEntity = RequestEntity.post(url)
                    .headers(httpHeaders)
                    .body(payload);

            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class).getBody();
        } else {
            throw new RuntimeException("Método não suportado: " + httpMethod);
        }

        return response;
    }


}
