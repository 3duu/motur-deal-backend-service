package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.config.app.security.cognito.CognitoService;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/login")
public class LoginEndpoint {
/*
    private final CognitoService cognitoService;

    public LoginEndpoint(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }*/
    @Operation(summary = "Autentica um usuário no sistema e retorna um token de acesso.")
    @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso.")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @PostMapping(name = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String login() {
        return null;//cognitoService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
    }
}
