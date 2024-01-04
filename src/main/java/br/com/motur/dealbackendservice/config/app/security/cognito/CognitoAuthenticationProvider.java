package br.com.motur.dealbackendservice.config.app.security.cognito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CognitoAuthenticationProvider implements /*AuthenticationProvider,*/ AuthenticationManager {

    @Autowired
    private CognitoService cognitoService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Aqui você verifica o usuário com o AWS Cognito
        // Se as credenciais forem inválidas, lance uma AuthenticationException
        cognitoService.loginUser(username, password);


        // Se as credenciais forem válidas, retorne um Authentication totalmente preenchido

        throw new AuthenticationException("Falha na autenticação") {};
    }

    //@Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
