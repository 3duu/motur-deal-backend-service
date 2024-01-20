package br.com.motur.dealbackendservice.core.model.common;

import lombok.Data;

@Data
public class OpenIDConnectAuthConfig {

    private String clientId;
    private String clientSecret;
    private String issuerUrl;
}
