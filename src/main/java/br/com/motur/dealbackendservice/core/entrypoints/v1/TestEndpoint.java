package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.utils.TokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/teste")
public class TestEndpoint {

    @Operation(summary = "Salva o anúncio para ser avaliado para publicação.",
            description = "Para que seja publicado, o anúncio passa por 3 validações. <br> " +
                    "Validação de négócio - Valida condições básicas de publicação <br> " +
                    "Validção de fraude - Verifica se o anúncio contém fraude <br> " +
                    "Validção de imagens - Valida se as imagens são condizentes com o pilar de integridade.")
    @ApiResponse(responseCode = "200", description = "Anuncio foi incluído com sucesso e está sendo processado para publicação.")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @GetMapping
    public String test(final JwtAuthenticationToken jwtAuthenticationToken){

        return TokenUtils.getAuthorizationToken(jwtAuthenticationToken);
    }
}
