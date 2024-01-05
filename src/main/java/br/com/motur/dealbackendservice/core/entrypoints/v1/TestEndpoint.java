package br.com.motur.dealbackendservice.core.entrypoints.v1;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class TestEndpoint {

    @GetMapping("/teste")
    public String test(KeycloakAuthenticationToken keycloakAuthenticationToken) {
        return "Teste";
    }
}
