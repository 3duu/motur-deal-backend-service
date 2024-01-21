package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.VehicleRepository;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.VehicleEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final IntegrationService integrationService;
    private final ProviderRepository providerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public VehicleService(final VehicleRepository vehicleRepository, IntegrationService integrationService, ProviderRepository providerRepository, ModelMapper modelMapper) {
        this.vehicleRepository = vehicleRepository;
        this.integrationService = integrationService;
        this.providerRepository = providerRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public VehicleEntity save(VehicleEntity vehicle) throws Exception {
        VehicleEntity savedVehicle = vehicleRepository.save(vehicle);
        // Chamar a integração para cada provedor configurado
        List<ProviderEntity> providers = providerRepository.findAll();
        for (ProviderEntity provider : providers) {
            integrationService.integrateVehicle(savedVehicle, provider);
        }
        return savedVehicle;
    }

    public List<VehicleEntity> findAll() {
        return vehicleRepository.findAll();
    }

    public Optional<VehicleEntity> findById(final Long id) {
        return vehicleRepository.findById(id);
    }

    public VehicleEntity update(final Long id, final VehicleEntity vehicleDetails) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {

                    modelMapper.map(vehicleDetails, vehicle);
                    return vehicleRepository.save(vehicle);
                })
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id " + id));
    }

    public void delete(final Long id) {
        vehicleRepository.deleteById(id);
    }

}
