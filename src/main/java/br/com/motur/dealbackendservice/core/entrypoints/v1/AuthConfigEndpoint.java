package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.core.entrypoints.CrudController;
import br.com.motur.dealbackendservice.core.model.AuthConfigEntity;
import br.com.motur.dealbackendservice.core.service.AuthConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.security.auth.message.config.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth-config")
public class AuthConfigEndpoint extends CrudController<AuthConfigEntity> {

    @Autowired
    private AuthConfigService authConfigService;

    @Operation(summary = "Criar uma nova configuração de autenticação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Configuração de autenticação criada",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthConfigEntity.class)))
            })
    @PostMapping
    public ResponseEntity<AuthConfigEntity> createAuthConfig(@RequestBody AuthConfigEntity authConfig) {
        AuthConfigEntity savedConfig = authConfigService.save(authConfig);
        return ResponseEntity.ok(savedConfig);
    }

    @Operation(summary = "Listar todas as configurações de autenticação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de configurações de autenticação",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthConfigEntity.class)))
            })
    @GetMapping
    public ResponseEntity<List<AuthConfigEntity>> getAllAuthConfigs() {
        List<AuthConfigEntity> authConfigs = authConfigService.findAll();
        return ResponseEntity.ok(authConfigs);
    }

    @Operation(summary = "Buscar uma configuração de autenticação pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Configuração de autenticação encontrada",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthConfigEntity.class))),
                    @ApiResponse(responseCode = "404", description = "Configuração de autenticação não encontrada")
            })
    @GetMapping("/{id}")
    public ResponseEntity<AuthConfigEntity> getAuthConfigById(@PathVariable Integer id) {
        AuthConfigEntity authConfig = authConfigService.findById(id)
                .orElseThrow(() -> new RuntimeException("AuthConfig not found"));
        return ResponseEntity.ok(authConfig);
    }

    @Operation(summary = "Atualizar uma configuração de autenticação existente",
              responses = {
        @ApiResponse(responseCode = "200", description = "Configuração de autenticação atualizada",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AuthConfig.class))),
        @ApiResponse(responseCode = "404", description = "Configuração de autenticação não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AuthConfigEntity> updateAuthConfig(@PathVariable Integer id, @RequestBody AuthConfigEntity authConfigDetails) {
        AuthConfigEntity updatedAuthConfig = authConfigService.update(id, authConfigDetails);
        return ResponseEntity.ok(updatedAuthConfig);
    }

    @Operation(summary = "Deletar uma configuração de autenticação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Configuração de autenticação deletada"),
                    @ApiResponse(responseCode = "404", description = "Configuração de autenticação não encontrada")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAuthConfig(@PathVariable Integer id) {
        authConfigService.delete(id);
        return ResponseEntity.ok().build();
    }
}
