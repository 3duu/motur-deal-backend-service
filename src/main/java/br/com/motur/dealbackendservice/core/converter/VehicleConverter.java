package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.common.ValueObjectConverter;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.VehicleDTO;
import br.com.motur.dealbackendservice.core.model.TrimEntity;
import br.com.motur.dealbackendservice.core.model.VehicleEntity;
import br.com.motur.dealbackendservice.core.service.TrimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VehicleConverter extends ValueObjectConverter<VehicleDTO, VehicleEntity> {

    private final TrimService trimService;

    @Autowired
    public VehicleConverter(TrimService trimService) {
        this.trimService = trimService;
    }

    @Override
    public VehicleEntity convert(final VehicleDTO dto) {
        VehicleEntity entity = new VehicleEntity();

        entity.setId(dto.getId()); // Supondo que você queira manter o mesmo ID

        // Busque e defina a entidade Trim com base no trimId
        TrimEntity trim = trimService.findById(dto.getTrimId());
        entity.setTrim(trim);

        entity.setModelYear(dto.getModelYear());
        entity.setProductionYear(dto.getProductionYear());
        entity.setFuelId(dto.getFuelId());
        entity.setTransmissionType(dto.getTransmissionType());
        entity.setLicensePlate(dto.getLicensePlate());
        entity.setColor(dto.getColor());
        entity.setKm(dto.getKm());
        entity.setPrice(BigDecimal.valueOf(dto.getPrice()));
        entity.setDescription(dto.getDescription());
        entity.setDealerId(dto.getDealerId());
        entity.setDealerCityId(dto.getDealerCityId());
        entity.setDealerState(dto.getDealerState());
        entity.setStatus(dto.getStatus());

        // As publicações devem ser convertidas ou mapeadas adequadamente
        /*entity.setPublication(dto.getPublication()
                .stream()
                .map(pub -> publicationConverter.convertToEntity(pub))
                .collect(Collectors.toList()));*/

        entity.setProviderIds(dto.getProviderIds());

        return entity;
    }



    @Override
    public VehicleDTO invert(VehicleEntity json) {
        return null;
    }

}
