package br.com.motur.dealbackendservice.core.model.common;

import lombok.Data;

@Data
public class OAuth2AuthConfig {
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
}

