package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.converter.ValueHelper;
import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.FieldMappingRepository;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.*;
import br.com.motur.dealbackendservice.core.service.vo.AdVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import java.util.*;

/**
 * Essa classe é responsável por executar integrações de anúncio com provedores
 */
@Service
public class IntegrationService {

    private final AuthConfigRepository authConfigRepository;
    private final FieldMappingRepository fieldMappingRepository;
    private final RestTemplate restTemplate;
    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper;

    @Autowired
    public IntegrationService(AuthConfigRepository authConfigRepository,
                              FieldMappingRepository fieldMappingRepository,
                              RestTemplate restTemplate, ApplicationContext applicationContext, ObjectMapper objectMapper) {
        this.authConfigRepository = authConfigRepository;
        this.fieldMappingRepository = fieldMappingRepository;
        this.restTemplate = restTemplate;
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> adaptVehicleToProviderFormat(final AdEntity ad, final AdVo adVo, final ProviderEntity provider, final List<FieldMappingEntity> fieldMappings) {
        final Map<String, Object> adaptedVehicle = new HashMap<>();

        final ProviderTrimsEntity providerTrim = ad.getAdPublicationList().stream().filter(adPublicationEntity -> adPublicationEntity.getProvider().getId().equals(provider.getId())).findFirst().orElse(null).getProviderTrimsEntity();
        final ProviderModelsEntity providerModel = (ProviderModelsEntity) providerTrim.getParentProviderCatalog();
        final ProviderBrandsEntity providerBrand = (ProviderBrandsEntity) providerModel.getParentProviderCatalog();

        for (FieldMappingEntity fieldMapping : fieldMappings) {

            String externalFieldName = fieldMapping.getExternalFieldName();
            final Object fieldValue = getFieldValue(adVo, fieldMapping, providerTrim, providerModel, providerBrand);

            adaptedVehicle.put(externalFieldName, convertValueToType(fieldValue, fieldMapping.getDataType(), providerTrim, providerModel, providerBrand));
        }

        // Adicionar campos adicionais de catalogo do provedor
        if (providerTrim != null){
            final FieldMappingEntity trimField = fieldMappings.stream().filter(f -> f.getLocalFieldName().equalsIgnoreCase("trim")).findFirst().orElse(null);
            if (trimField != null) {
                adaptedVehicle.put(trimField.getExternalFieldName(), providerTrim.getExternalId());
            }

            final FieldMappingEntity modelField = fieldMappings.stream().filter(f -> f.getLocalFieldName().equalsIgnoreCase("model")).findFirst().orElse(null);
            if (modelField != null) {
                adaptedVehicle.put(modelField.getExternalFieldName(), providerModel.getExternalId());
            }

            final FieldMappingEntity brandField = fieldMappings.stream().filter(f -> f.getLocalFieldName().equalsIgnoreCase("brand")).findFirst().orElse(null);
            if (brandField != null) {
                adaptedVehicle.put(brandField.getExternalFieldName(), providerBrand.getExternalId());
            }
        }

        return adaptedVehicle;
    }

    /**
     * Obtém o valor de um campo privado de um objeto.
     *
     * @param ad   O objeto do qual o valor do campo deve ser obtido.
     * @param fieldMapping O nome do campo.
     * @return O valor do campo.
     */
    private Object getFieldValue(final AdVo ad, final FieldMappingEntity fieldMapping, final ProviderTrimsEntity providerTrim, final ProviderModelsEntity providerModel, final ProviderBrandsEntity providerBrand) {
        try {

            final List<Field> fields = Arrays.stream(ad.getClass().getDeclaredFields()).toList();

            final Field field = fields.stream().filter(f -> f.getName().equalsIgnoreCase(fieldMapping.getLocalFieldName())).findFirst().orElse(null);
            if (field != null) {

                final FieldMappingInfo fieldMappingInfo = field.getAnnotation(FieldMappingInfo.class);
                if (fieldMappingInfo != null) {

                    if (fieldMappingInfo.helper() != null && fieldMappingInfo.helper().length > 0) {
                        final ValueHelper helper = applicationContext.getBean(fieldMappingInfo.helper()[0]);
                        if (helper != null) {
                            return helper.getDefaultValue(ad, fieldMappingInfo);
                        }
                    }
                    else {

                        if (fieldMappingInfo.type() == DataType.ID) {

                            Entity entity = ad.getClass().getAnnotation(Entity.class);
                            if (entity != null) {

                                for(var fieldEntity : field.get(ad).getClass().getDeclaredFields()) {
                                    if (fieldEntity.getAnnotation(Id.class) != null || fieldEntity.getAnnotation(org.springframework.data.annotation.Id.class) != null) {
                                        return fieldEntity.get(ad).toString();
                                    }
                                }

                                return field.get(ad).toString();
                            }

                            //return List.of(field.get(ad).toString().split(","));
                        }

                    }
                }
                field.setAccessible(true);
                return field.get(ad);
            }
            else {

                if (fieldMapping.getDataType() == DataType.MAP) {
                    final Map<String, Object> innerFields = new JSONObject(fieldMapping.getLocalFieldName()).toMap();
                    final Map<String, Object> returnValue = new HashMap<>();

                    innerFields.forEach((k, v) -> {
                        try {
                                for (Field f : fields) {

                                    final FieldMappingInfo fieldMappingInfo = f.getAnnotation(FieldMappingInfo.class);
                                    if (fieldMappingInfo != null) {

                                        if (v.toString().contains("#")){
                                            returnValue.put(k, getValueFromNestedFields(ad, v.toString()));
                                        }
                                        else if (f.getName().equalsIgnoreCase(fieldMapping.getLocalFieldName())) {
                                            returnValue.put(k, f.get(ad));
                                        }
                                        else{
                                            returnValue.put(k, v);
                                        }

                                        if (fieldMappingInfo.helper() != null && fieldMappingInfo.helper().length > 0) {
                                            final ValueHelper helper = applicationContext.getBean(fieldMappingInfo.helper()[0]);
                                            if (helper != null) {
                                                returnValue.put(k, helper.getDefaultValue(ad, fieldMappingInfo));
                                            }
                                        }

                                    }
                                }
                        } catch (Exception e) {

                            //logger
                            e.printStackTrace();
                        }
                    });

                    fields.forEach(f -> {
                        try {
                            var v = innerFields.get(f.getName());
                            if (v != null){
                                f.setAccessible(true);
                                //returnValue.put();//f.get(ad);
                            }

                            //if (f.getName().equalsIgnoreCase(fieldMapping.getLocalFieldName()) {

                            //map.put(f.getName(), f.get(ad));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    return returnValue;
                }
            }

        } catch (/*NoSuchFieldException |*/ IllegalAccessException e) {
            //e.printStackTrace();
            // Tratamento adequado de exceções ou retorno de um valor padrão
            return null;
        }

        return null;
    }

    /**
     * Obtém o valor de um campo privado de um objeto.
     *
     * @param obj   O objeto do qual o valor do campo deve ser obtido.
     * @param fieldsStr O nome do campo.
     * @return O valor do campo.
     */
    private Object getValueFromNestedFields(final AdVo obj, String fieldsStr) {
        final String[] fields = fieldsStr.split("#");
        Object currentObj = obj;

        for (String fieldName : fields) {
            Field field = null;

            for (Field f : currentObj.getClass().getDeclaredFields()) {

                final FieldMappingInfo fieldMappingInfo = f.getAnnotation(FieldMappingInfo.class);
                if (fieldMappingInfo != null && fieldMappingInfo.name().equals(fieldName)){
                    field = f;

                    try {

                        field.setAccessible(true);
                        if (fieldMappingInfo.helper() != null && fieldMappingInfo.helper().length > 0) {
                            final ValueHelper helper = applicationContext.getBean(fieldMappingInfo.helper()[0]);
                            if (helper != null) {
                                currentObj = helper.getDefaultValue(obj, fieldMappingInfo);
                                break;
                            }
                        }

                        currentObj = field.get(currentObj);
                        break;

                    } catch (IllegalAccessException e) {
                        return null;
                    }

                    //break;
                }
            }


        }

        return currentObj;
    }


    /**
     * Converte um valor para um tipo de dados.
     *
     * @param value         O valor a ser convertido.
     * @param type          O tipo de dados para o qual o valor deve ser convertido.
     * @param providerTrim
     * @param providerModel
     * @param providerBrand
     * @return O valor convertido.
     */
    private Object convertValueToType(Object value, DataType type, ProviderTrimsEntity providerTrim, ProviderModelsEntity providerModel, ProviderBrandsEntity providerBrand) {
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
                case MAP:
                    return objectMapper.convertValue(value, Map.class);
                default:
                    return value;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Autentica com o provedor e retorna o token de autenticação.
     *
     * @param authConfig A configuração de autenticação para o provedor.
     * @return O token de autenticação.
     */
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

    /**
     * Autentica com o provedor usando OAuth 2.0.
     * @param authConfig
     * @return
     */
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

    private String authenticateWithBearerToken(AuthConfigEntity authConfig) {
        BearerTokenAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), BearerTokenAuthConfig.class);
        return "Bearer ".concat(config.getToken());
    }

    private String authenticateWithDigest(AuthConfigEntity authConfig) {
        DigestAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), DigestAuthConfig.class);
        // Implemente a lógica de autenticação Digest aqui
        return ""; // Retorne o valor apropriado
    }

    private String authenticateWithJwt(AuthConfigEntity authConfig) {
        JWTAuthConfig config = objectMapper.convertValue(authConfig.getDetails(), JWTAuthConfig.class);
        return "Bearer ".concat(config.getJwtToken());
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



    private void sendVehicleData(String providerApiUrl, Map<String, Object> vehicleData, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(vehicleData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(providerApiUrl, requestEntity, String.class);
            // Verifique a resposta. Em caso de erro, lidar adequadamente.
        } catch (Exception e) {
            //e.printStackTrace(); // Log e tratamento adequado de erros.
        }
    }

    private String getProviderApiUrl(final ProviderEntity provider) {
        // Retorne a URL da API com base no providerId. Isso pode envolver uma consulta ao banco de dados ou uma configuração.
        return "http://example.com/api"; // Exemplo. Substitua pela lógica real.
    }

    public void sendDataToProvider(String adData, ProviderEntity provider) {
    }
}
