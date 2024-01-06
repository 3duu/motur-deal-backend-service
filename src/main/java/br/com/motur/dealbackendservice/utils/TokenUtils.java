package br.com.motur.dealbackendservice.utils;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class TokenUtils {

    private TokenUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getAuthorizationToken(final JwtAuthenticationToken jwtAuthenticationToken) {

        return jwtAuthenticationToken.getToken().getTokenValue();
    }
}
