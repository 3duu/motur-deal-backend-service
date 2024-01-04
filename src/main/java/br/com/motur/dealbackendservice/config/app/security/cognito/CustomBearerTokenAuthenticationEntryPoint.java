package br.com.motur.dealbackendservice.config.app.security.cognito;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomBearerTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Personalize sua resposta aqui. Por exemplo, enviar uma mensagem de erro personalizada, definir cabeçalhos, etc.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print("Erro de Autenticação: " + authException.getMessage());
    }
}