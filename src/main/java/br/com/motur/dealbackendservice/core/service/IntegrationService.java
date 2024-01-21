package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.FieldMappingRepository;
import br.com.motur.dealbackendservice.core.model.AuthConfigEntity;
import br.com.motur.dealbackendservice.core.model.FieldMappingEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.VehicleEntity;
import br.com.motur.dealbackendservice.core.model.common.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class IntegrationService {

    private final AuthConfigRepository authConfigRepository;
    private final FieldMappingRepository fieldMappingRepository;
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public IntegrationService(AuthConfigRepository authConfigRepository,
                              FieldMappingRepository fieldMappingRepository,
                              RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.authConfigRepository = authConfigRepository;
        this.fieldMappingRepository = fieldMappingRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void integrateVehicle(VehicleEntity vehicle, final ProviderEntity provider) throws Exception {
        // Encontrar a configuração de autenticação e mapeamento de campos para o provedor
        //AuthConfigEntity authConfig = authConfigRepository.findByProviderId(providerId);
        List<FieldMappingEntity> fieldMappings = fieldMappingRepository.findByProviderId(provider.getId());

        // Adaptar o veículo para o formato esperado pela API do provedor
        Object providerVehicle = adaptVehicleToProviderFormat(vehicle, provider, fieldMappings);

        // Realizar a autenticação e enviar a solicitação para a API do provedor
        //String token = authenticateWithProvider(authConfig);
        sendVehicleToProvider(vehicle, provider);
    }

    public Map<String, Object> adaptVehicleToProviderFormat(final VehicleEntity vehicle, final ProviderEntity provider, final List<FieldMappingEntity> fieldMappings) {
        Map<String, Object> adaptedVehicle = new HashMap<>();

        if (vehicle.getProviderIds().contains(provider.getId())) {
            for (FieldMappingEntity fieldMapping : fieldMappings) {
                if (fieldMapping.getProvider().getId().equals(provider.getId())) {
                    String localFieldName = fieldMapping.getLocalFieldName();
                    String externalFieldName = fieldMapping.getExternalFieldName();
                    final Object fieldValue = getFieldValue(vehicle, localFieldName);

                    adaptedVehicle.put(externalFieldName, convertValueToType(fieldValue, fieldMapping.getDataType()));
                }
            }
        }

        return adaptedVehicle;
    }

    private Object getFieldValue(final VehicleEntity vehicle, final String fieldName) {
        try {
            Field field = VehicleEntity.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(vehicle);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            // Tratamento adequado de exceções ou retorno de um valor padrão
            return null;
        }
    }


    private Object convertValueToType(Object value, DataType type) {
        if (value == null) {
            return null;
        }

        try {
            switch (type) {
                case BYTE:
                    return Byte.parseByte(value.toString());
                case SHORT:
                    return Short.parseShort(value.toString());
                case INT:
                    return Integer.parseInt(value.toString());
                case LONG:
                    return Long.parseLong(value.toString());
                case FLOAT:
                    return Float.parseFloat(value.toString());
                case DOUBLE:
                    return Double.parseDouble(value.toString());
                case BOOLEAN:
                    return Boolean.parseBoolean(value.toString());
                case CHAR:
                    return value.toString().charAt(0);
                case STRING:
                    return value.toString();
                case DATE:
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    return dateFormat.parse(value.toString());
                case LOCAL_DATETIME:
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    return LocalDateTime.parse(value.toString(), dateTimeFormatter);
                case BIG_DECIMAL:
                    return new BigDecimal(value.toString());
                case LIST:
                    // Supondo que o valor é uma lista de Strings
                    return List.of(value.toString().split(","));
                case JSON:
                    return new JSONObject(value.toString());
                default:
                    return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Lide com a exceção ou retorne null
            return null;
        }
    }


    public String authenticateWithProvider(final AuthConfigEntity authConfig) throws Exception {
        switch (authConfig.getAuthType()) {
            case OAUTH2:
                return authenticateWithOAuth(authConfig);
            case BASIC:
                return authenticateWithBasic(authConfig);
            case API_KEY:
                return authenticateWithApiKey(authConfig); // Assumindo que a chave da API está na AuthConfig
            case BEARER_TOKEN:
                return "Bearer " + authenticateWithBearerToken(authConfig); // Assumindo que o token Bearer está na AuthConfig
            case JWT:
                return authenticateWithJwt(authConfig); // Assumindo que o token JWT está na AuthConfig
            case DIGEST:
                return authenticateWithDigest(authConfig); // Implemente a lógica de autenticação Digest aqui
            case NTLM:
                return authenticateWithNtlm(authConfig); // Implemente a lógica de autenticação NTLM aqui
            case SAML:
                return authenticateWithSaml(authConfig); // Implemente a lógica de autenticação SAML aqui
            case OPENID_CONNECT:    // Implemente a lógica de autenticação OpenID Connect aqui
                return authenticateWithOpenIDConnect(authConfig);
            // Adicione casos para outros tipos de autenticação conforme necessário
            case CUSTOM:
                // Implemente sua lógica personalizada de autenticação
                return authenticateWithCustom(authConfig);
            default:
                throw new IllegalArgumentException("Tipo de autenticação não suportado: " + authConfig.getAuthType().getDisplayName());
        }
    }

    private String authenticateWithOAuth(AuthConfigEntity authConfig) {

        final OAuth2AuthConfig details = objectMapper.convertValue(authConfig.getDetails(), OAuth2AuthConfig.class);
        String clientId = details.getClientId();
        String clientSecret = details.getClientSecret();
        String tokenUrl = details.getTokenUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        // Extraindo token do response (depende do formato da resposta)
        return response.getBody(); // Modifique conforme a estrutura da resposta
    }

    private String authenticateWithBasic(AuthConfigEntity authConfig) {
        final BasicAuthConfig details = objectMapper.convertValue(authConfig.getDetails(), BasicAuthConfig.class);
        String username = details.getUsername();
        String password = details.getPassword();

        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    private String authenticateWithApiKey(AuthConfigEntity authConfig) {
        final ApiKeyAuthConfig details = objectMapper.convertValue(authConfig.getDetails(), ApiKeyAuthConfig.class);
        String apiKey = details.getApiKey();
        return apiKey;
    }

    private String authenticateWithBearerToken(AuthConfigEntity authConfig) throws Exception {
        BearerTokenAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), BearerTokenAuthConfig.class);
        return "Bearer " + config.getToken();
    }

    private String authenticateWithDigest(AuthConfigEntity authConfig) throws Exception {
        DigestAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), DigestAuthConfig.class);
        // Implemente a lógica de autenticação Digest aqui
        return ""; // Retorne o valor apropriado
    }

    private String authenticateWithJwt(AuthConfigEntity authConfig) throws Exception {
        JWTAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), JWTAuthConfig.class);
        return "Bearer " + config.getJwtToken();
    }

    private String authenticateWithSaml(AuthConfigEntity authConfig) throws Exception {
        SAMLAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), SAMLAuthConfig.class);
        // Implemente a lógica de autenticação SAML aqui
        return ""; // Retorne o valor apropriado
    }

    private String authenticateWithOpenIDConnect(AuthConfigEntity authConfig) throws Exception {
        OpenIDConnectAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), OpenIDConnectAuthConfig.class);
        // Implemente a lógica de autenticação OpenID Connect aqui
        return ""; // Retorne o valor apropriado
    }

    private String authenticateWithNtlm(AuthConfigEntity authConfig) throws Exception {
        NTLMAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), NTLMAuthConfig.class);
        // Implemente a lógica de autenticação NTLM aqui
        return ""; // Retorne o valor apropriado
    }

    private String authenticateWithCustom(AuthConfigEntity authConfig) throws Exception {
        CustomAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), CustomAuthConfig.class);
        // Implemente sua lógica de autenticação personalizada aqui
        return ""; // Retorne o valor apropriado
    }

    /**
     * Envia o veículo para o provedor.
     *
     * @param vehicle    O veículo a ser enviado.
     * @param provider O ID do provedor para o qual o veículo deve ser enviado.
     */
    public void sendVehicleToProvider(final VehicleEntity vehicle, final ProviderEntity provider) throws Exception {
        // Obter configuração de autenticação
        final AuthConfigEntity authConfig = authConfigRepository.findByProviderId(provider.getId());
        if (authConfig == null) {
            throw new IllegalStateException("Configuração de autenticação não encontrada para o provedor: " + provider.getId());
        }

        final List<FieldMappingEntity> fieldMappings = fieldMappingRepository.findByProviderId(provider.getId());

        final Map<String, Object> vehicleData = adaptVehicleToProviderFormat(vehicle, provider, fieldMappings);

        // Autenticar com o provedor e obter o token
        String authToken = authenticateWithProvider(authConfig);

        // Preparar a URL e os dados do veículo
        String providerApiUrl = getProviderApiUrl(provider);

        String createAdUrl = provider.getCreateAdEndpoint();

        // Enviar veículo
        sendVehicleData(createAdUrl, vehicleData, authToken);
    }

    private void sendVehicleData(String providerApiUrl, Map<String, Object> vehicleData, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(vehicleData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(providerApiUrl, requestEntity, String.class);
            // Verifique a resposta. Em caso de erro, lidar adequadamente.
        } catch (Exception e) {
            e.printStackTrace(); // Log e tratamento adequado de erros.
        }
    }

    private String getProviderApiUrl(final ProviderEntity provider) {
        // Retorne a URL da API com base no providerId. Isso pode envolver uma consulta ao banco de dados ou uma configuração.
        return "http://example.com/api"; // Exemplo. Substitua pela lógica real.
    }
}
