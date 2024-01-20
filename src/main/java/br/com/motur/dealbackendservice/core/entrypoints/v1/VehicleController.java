package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.core.model.VehicleEntity;
import br.com.motur.dealbackendservice.core.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<VehicleEntity> createVehicle(@RequestBody VehicleEntity vehicle) throws Exception {
        VehicleEntity savedVehicle = vehicleService.save(vehicle);
        return ResponseEntity.ok(savedVehicle);
    }

    @GetMapping
    public ResponseEntity<List<VehicleEntity>> getAllVehicles() {
        List<VehicleEntity> vehicles = vehicleService.findAll();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleEntity> getVehicleById(@PathVariable Long id) {
        return vehicleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleEntity> updateVehicle(@PathVariable Long id, @RequestBody VehicleEntity vehicleDetails) {
        VehicleEntity updatedVehicle = vehicleService.update(id, vehicleDetails);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.ok().build();
    }
}
