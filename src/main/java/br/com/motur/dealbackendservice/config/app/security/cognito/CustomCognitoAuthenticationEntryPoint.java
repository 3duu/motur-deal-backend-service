package br.com.motur.dealbackendservice.config.app.security.cognito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
public class CustomCognitoAuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        if (authException instanceof OAuth2AuthenticationException) {
            // Personalize sua resposta aqui para exceções de autenticação OAuth2
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro de Autenticação OAuth2");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autorizado");
        }
    }
}
