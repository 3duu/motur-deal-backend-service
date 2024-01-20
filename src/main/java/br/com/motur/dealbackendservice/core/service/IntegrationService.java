package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.FieldMappingRepository;
import br.com.motur.dealbackendservice.core.model.AuthConfigEntity;
import br.com.motur.dealbackendservice.core.model.FieldMappingEntity;
import br.com.motur.dealbackendservice.core.model.VehicleEntity;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    @Autowired
    public IntegrationService(AuthConfigRepository authConfigRepository,
                              FieldMappingRepository fieldMappingRepository,
                              RestTemplate restTemplate) {
        this.authConfigRepository = authConfigRepository;
        this.fieldMappingRepository = fieldMappingRepository;
        this.restTemplate = restTemplate;
    }

    public void integrateVehicle(VehicleEntity vehicle, final Integer providerId) {
        // Encontrar a configuração de autenticação e mapeamento de campos para o provedor
        AuthConfigEntity authConfig = authConfigRepository.findByProviderId(providerId);
        List<FieldMappingEntity> fieldMappings = fieldMappingRepository.findByProviderId(providerId);

        // Adaptar o veículo para o formato esperado pela API do provedor
        Object providerVehicle = adaptVehicleToProviderFormat(vehicle, providerId, fieldMappings);

        // Realizar a autenticação e enviar a solicitação para a API do provedor
        String token = authenticateWithProvider(authConfig);
        sendVehicleToProvider(providerVehicle, authConfig, token);
    }

    public Map<String, Object> adaptVehicleToProviderFormat(VehicleEntity vehicle, Integer providerId, List<FieldMappingEntity> fieldMappings) {
        Map<String, Object> adaptedVehicle = new HashMap<>();

        if (vehicle.getProviderIds().contains(providerId)) {
            for (FieldMappingEntity fieldMapping : fieldMappings) {
                if (fieldMapping.getProvider().getId().equals(providerId)) {
                    String localFieldName = fieldMapping.getLocalFieldName();
                    String externalFieldName = fieldMapping.getExternalFieldName();
                    Object fieldValue = getFieldValue(vehicle, localFieldName);

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

    private String authenticateWithProvider(AuthConfigEntity authConfig) {
        // Implementar a lógica para autenticar com o provedor e retornar o token
    }

    public String authenticateWithProvider(AuthConfigEntity authConfig) {
        switch (authConfig.getAuthType()) {
            case OAUTH2:
                return authenticateWithOAuth(authConfig);
            case BASIC:
                return authenticateWithBasicAuth(authConfig);
            case API_KEY:
                return authConfig.getApiKey(); // Assumindo que a chave da API está na AuthConfig
            case BEARER_TOKEN:
                return "Bearer " + authConfig.getBearerToken(); // Assumindo que o token Bearer está na AuthConfig
            // Adicione casos para outros tipos de autenticação conforme necessário
            case CUSTOM:
                // Implemente sua lógica personalizada de autenticação
                return customAuthenticationMethod(authConfig);
            default:
                throw new IllegalArgumentException("Tipo de autenticação não suportado: " + authConfig.getAuthType().getDisplayName());
        }
    }

    private String authenticateWithOAuth(AuthConfigEntity authConfig) {
        // Implementar lógica de autenticação OAuth
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("client_id", authConfig.getClientId());
        requestBody.put("client_secret", authConfig.getClientSecret());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(authConfig.getTokenUrl(), request, String.class);

        // Extraindo token do response (depende do formato da resposta)
        // Supondo que o token está no corpo da resposta como um campo "access_token"
        // Esta parte pode variar dependendo do provedor de OAuth
        return response.getBody(); // Modifique conforme a estrutura da resposta
    }

    private String authenticateWithBasicAuth(AuthConfigEntity authConfig) {
        // Implementar lógica de autenticação Basic Auth
        // Geralmente, Basic Auth não necessita de uma etapa extra de autenticação como OAuth
        // A codificação do cabeçalho de autorização é normalmente feita em cada solicitação
        String credentials = authConfig.getUsername() + ":" + authConfig.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    // Métodos para outros tipos de autenticação...

    private String customAuthenticationMethod(AuthConfigEntity authConfig) {
        // Implemente seu método personalizado de autenticação
    }

    private void sendVehicleToProvider(Object providerVehicle, AuthConfigEntity authConfig, String token) {
        // Implementar a lógica para enviar o veículo adaptado à API do provedor
    }
}
