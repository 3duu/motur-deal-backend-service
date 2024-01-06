package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.core.entrypoints.v1.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Login do usuário.",
            description = "")
    @ApiResponse(responseCode = "200", description = "")
    @PostMapping(value = "authenticate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String login(@RequestBody LoginRequest loginRequest){
        return loginRequest.getUsername();
    }
}
