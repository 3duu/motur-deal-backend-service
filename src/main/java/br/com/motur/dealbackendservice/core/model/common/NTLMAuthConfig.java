package br.com.motur.dealbackendservice.core.model.common;

import lombok.Data;

@Data
public class NTLMAuthConfig {

    private String username;
    private String password;
    private String domain;
}
