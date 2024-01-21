package br.com.motur.dealbackendservice.core.model.common;

public enum AuthType {

    BASIC("Basic"), //0
    OAUTH2("OAuth2"), //1
    API_KEY("API Key"), //2
    BEARER_TOKEN("Bearer Token"), //3
    DIGEST("Digest"), //4
    JWT("JWT"), // JSON Web Token
    SAML("SAML"), // Security Assertion Markup Language
    OPENID_CONNECT("OpenID Connect"),
    NTLM("NTLM"), // Windows-based authentication
    QUERY_PARAMS("Query Params"), // Para métodos de autenticação que utilizam parâmetros na URL
    CUSTOM("Custom"); // Para métodos personalizados de autenticação

    private final String displayName;

    AuthType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
