package br.com.motur.dealbackendservice.config.app.security.cognito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
public class CustomCognitoAccessDeniedHandler /*extends OAuth2AccessDeniedHandler */{

    //@Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // Personalize sua resposta aqui. Por exemplo, você pode definir um status HTTP personalizado, enviar uma mensagem de erro, etc.
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().print("Acesso Negado: " + accessDeniedException.getMessage());
    }
}
