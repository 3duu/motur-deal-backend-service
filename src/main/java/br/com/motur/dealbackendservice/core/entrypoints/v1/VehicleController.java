package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.core.entrypoints.CrudController;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.VehicleDTO;
import br.com.motur.dealbackendservice.core.model.VehicleEntity;
import br.com.motur.dealbackendservice.core.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController extends CrudController<VehicleEntity> {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Cria um novo veículo", responses = {
            @ApiResponse(responseCode = "200", description = "Veículo criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleDTO.class)))
    })
    @PostMapping
    public ResponseEntity<VehicleEntity> createVehicle(@RequestBody VehicleDTO vehicle) throws Exception {
        VehicleEntity ent = (VehicleEntity)getConverter().convert(vehicle);
        VehicleEntity savedVehicle = vehicleService.save(ent);
        return ResponseEntity.ok(savedVehicle);
    }

    @Operation(summary = "Lista todos os veículos", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de veículos obtida com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleEntity.class)))
    })
    @GetMapping
    public ResponseEntity<List<VehicleEntity>> getAllVehicles() {
        List<VehicleEntity> vehicles = vehicleService.findAll();
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Obtém um veículo pelo ID", responses = {
            @ApiResponse(responseCode = "200", description = "Veículo encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleEntity.class))),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleEntity> getVehicleById(@PathVariable Long id) {
        return vehicleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualiza um veículo", responses = {
            @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<VehicleEntity> updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDetails) {
        VehicleEntity ent = (VehicleEntity)getConverter().convert(vehicleDetails);
        VehicleEntity updatedVehicle = vehicleService.update(id, ent);
        return ResponseEntity.ok(updatedVehicle);
    }

    @Operation(summary = "Exclui um veículo", responses = {
            @ApiResponse(responseCode = "200", description = "Veículo excluído com sucesso")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.ok().build();
    }
}
