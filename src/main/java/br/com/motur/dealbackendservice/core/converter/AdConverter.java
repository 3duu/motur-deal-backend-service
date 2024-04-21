package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.common.ValueObjectConverter;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.AdDto;
import br.com.motur.dealbackendservice.core.model.AdEntity;
import br.com.motur.dealbackendservice.core.model.DealerEntity;
import br.com.motur.dealbackendservice.core.service.TrimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AdConverter extends ValueObjectConverter<AdDto, AdEntity> {

    private final TrimService trimService;

    @Autowired
    public AdConverter(TrimService trimService) {
        this.trimService = trimService;
    }

    @Override
    public AdEntity convert(final AdDto dto) {


        return AdEntity.builder()
                .id(dto.getId())
                .trimId(dto.getTrimId())
                .km(dto.getKm())
                .price(dto.getPrice())
                .color(dto.getColor())
                .description(dto.getDescription())
                .modelYear(dto.getModelYear())
                .productionYear(dto.getProductionYear())
                .fuelType(dto.getFuelType())
                .transmissionType(dto.getTransmissionType())
                .licensePlate(dto.getLicensePlate())
                .brandId(dto.getBrandId())
                .modelId(dto.getModelId())
                .status(dto.getStatus())
                .dealer(DealerEntity.builder().id(dto.getDealerId()).build())
                .build();
    }

    @Override
    public AdDto invert(final AdEntity source) {
        return AdDto.builder()
                .id(source.getId())
                .trimId(source.getTrimId())
                .km(source.getKm())
                .price(source.getPrice())
                .color(source.getColor())
                .description(source.getDescription())
                .modelYear(source.getModelYear())
                .productionYear(source.getProductionYear())
                .fuelType(source.getFuelType())
                .transmissionType(source.getTransmissionType())
                .licensePlate(source.getLicensePlate())
                .brandId(source.getBrandId())
                .modelId(source.getModelId())
                .status(source.getStatus())
                .dealerId(source.getDealer().getId())
                .build();
    }
}
