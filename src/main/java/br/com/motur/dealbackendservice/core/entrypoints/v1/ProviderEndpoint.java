package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.core.entrypoints.CrudController;
import br.com.motur.dealbackendservice.core.entrypoints.v1.pojo.config.Provider;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.service.ProviderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/providers")
public class ProviderEndpoint extends CrudController<Provider> {

    private final ProviderServiceImpl providerService;

    public ProviderEndpoint(final ProviderServiceImpl providerService) {
        this.providerService = providerService;
    }

    // GET para buscar todos os provedores
    @Operation(summary = "Listar todos os provedores", responses = {
            @ApiResponse(description = "Provedores encontrados", responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Provider.class)))
    })
    @GetMapping
    public ResponseEntity<List<ProviderEntity>> getAllProviders() {

        List<ProviderEntity> providers = providerService.findAll();
        return ResponseEntity.ok(providers);
    }

    // POST para criar um novo provedor
    @Operation(summary = "Criar um novo provedor", responses = {
            @ApiResponse(description = "Provedor criado com sucesso", responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Provider.class)))
    })
    @PostMapping
    public ResponseEntity<ProviderEntity> createProvider(@RequestBody ProviderEntity provider) {
        ProviderEntity savedProvider = providerService.save(provider);
        return ResponseEntity.ok(savedProvider);
    }

    // GET para buscar um provedor pelo ID
    @Operation(summary = "Buscar um provedor pelo ID", responses = {
            @ApiResponse(description = "Provedor encontrado", responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Provider.class))),
            @ApiResponse(description = "Provedor não encontrado", responseCode = "404")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProviderEntity> getProviderById(@PathVariable Integer id) {
        Optional<ProviderEntity> provider = providerService.findById(id);
        return provider.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT para atualizar um provedor existente
    @Operation(summary = "Atualizar um provedor existente", responses = {
            @ApiResponse(description = "Provedor atualizado com sucesso", responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Provider.class))),
            @ApiResponse(description = "Provedor não encontrado", responseCode = "404")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProviderEntity> updateProvider(@PathVariable final Integer id, @RequestBody final ProviderEntity providerDetails) {
        ProviderEntity updatedProvider = providerService.update(id, providerDetails);
        return ResponseEntity.ok(updatedProvider);
    }

    // DELETE para remover um provedor
    @Operation(summary = "Deletar um provedor", responses = {
            @ApiResponse(description = "Provedor deletado com sucesso", responseCode = "200"),
            @ApiResponse(description = "Provedor não encontrado", responseCode = "404")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvider(@PathVariable Integer id) {
        providerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
