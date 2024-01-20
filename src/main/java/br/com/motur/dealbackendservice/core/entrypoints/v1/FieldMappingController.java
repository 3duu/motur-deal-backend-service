package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.core.model.FieldMappingEntity;
import br.com.motur.dealbackendservice.core.service.FieldMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.motur.dealbackendservice.core.entrypoints.CrudController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/field-mapping")
public class FieldMappingController extends CrudController<FieldMappingEntity> {

    private final FieldMappingService fieldMappingService;

    @Autowired
    public FieldMappingController(FieldMappingService fieldMappingService) {
        this.fieldMappingService = fieldMappingService;
    }

    @Operation(summary = "Cria um novo mapeamento de campo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mapeamento de campo criado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = FieldMappingEntity.class)))
            })
    @PostMapping
    public ResponseEntity<FieldMappingEntity> createFieldMapping(@RequestBody final FieldMappingEntity fieldMapping) {
        FieldMappingEntity savedFieldMapping = fieldMappingService.save(fieldMapping);
        return ResponseEntity.ok(savedFieldMapping);
    }

    @Operation(summary = "Lista todos os mapeamentos de campo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de mapeamentos de campo",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = FieldMappingEntity[].class)))
            })
    @GetMapping
    public ResponseEntity<List<FieldMappingEntity>> getAllFieldMappings() {
        List<FieldMappingEntity> fieldMappings = fieldMappingService.findAll();
        return ResponseEntity.ok(fieldMappings);
    }

    @Operation(summary = "Busca um mapeamento de campo pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mapeamento de campo encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = FieldMappingEntity.class))),
                    @ApiResponse(responseCode = "404", description = "Mapeamento de campo não encontrado")
            })
    @GetMapping("/{id}")
    public ResponseEntity<FieldMappingEntity> getFieldMappingById(@PathVariable final Integer id) {
        return fieldMappingService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualiza um mapeamento de campo existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mapeamento de campo atualizado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = FieldMappingEntity.class))),
                    @ApiResponse(responseCode = "404", description = "Mapeamento de campo não encontrado")
            })
    @PutMapping("/{id}")
    public ResponseEntity<FieldMappingEntity> updateFieldMapping(@PathVariable final Integer id, @RequestBody final FieldMappingEntity fieldMappingDetails) {
        FieldMappingEntity updatedFieldMapping = fieldMappingService.update(id, fieldMappingDetails);
        return ResponseEntity.ok(updatedFieldMapping);
    }

    @Operation(summary = "Deleta um mapeamento de campo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mapeamento de campo deletado"),
                    @ApiResponse(responseCode = "404", description = "Mapeamento de campo não encontrado")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFieldMapping(@PathVariable Integer id) {
        fieldMappingService.delete(id);
        return ResponseEntity.ok().build();
    }
}
