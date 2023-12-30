package br.com.motur.dealbackendservice.core.model.common;

public enum AuthType {

    BASIC("Basic"),
    OAUTH2("OAuth2"),
    API_KEY("API Key"),
    BEARER_TOKEN("Bearer Token"),
    DIGEST("Digest"),
    JWT("JWT"), // JSON Web Token
    SAML("SAML"), // Security Assertion Markup Language
    OPENID_CONNECT("OpenID Connect"),
    NTLM("NTLM"), // Windows-based authentication
    CUSTOM("Custom"); // Para métodos personalizados de autenticação

    private final String displayName;

    AuthType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
