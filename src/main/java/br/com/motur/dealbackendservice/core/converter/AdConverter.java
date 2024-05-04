package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.common.ValueObjectConverter;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.AdDto;
import br.com.motur.dealbackendservice.core.model.AdEntity;
import br.com.motur.dealbackendservice.core.model.AdPublicationEntity;
import br.com.motur.dealbackendservice.core.model.DealerEntity;
import br.com.motur.dealbackendservice.core.service.TrimService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AdConverter extends ValueObjectConverter<AdDto, AdEntity> {

    private final TrimService trimService;
    private final ProviderRepository providerRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public AdConverter(TrimService trimService, ProviderRepository providerRepository, ObjectMapper objectMapper) {
        this.trimService = trimService;
        this.providerRepository = providerRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public AdEntity convert(final AdDto dto) {

        final AdEntity adEntity = AdEntity.builder()
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
                .mileage(dto.getMileage())
                .dealer(DealerEntity.builder().id(dto.getDealerId()).build())
                .adPublicationList(dto.getPublications().stream().map(pubDto -> {
                    try {
                        return AdPublicationEntity.builder()
                                .id(pubDto.getId())
                                .externalId(pubDto.getExternalId())
                                .providerTrimsEntity(trimService.findProviderEntityById(pubDto.getProviderTrimId(), pubDto.getProviderId()))
                                .planId(pubDto.getPlanId())
                                .additionalInfo(pubDto.getAdditionalInfo() != null ? objectMapper.convertValue(objectMapper.writeValueAsString(pubDto.getAdditionalInfo()), JsonNode.class)  : null)
                                .provider(providerRepository.findById(pubDto.getProviderId()).orElseThrow(NotFoundException::new))
                                .build();
                    } catch (NotFoundException e) {
                        throw new NotFoundException("Integrador com id: " + pubDto.getProviderId()  +" não encontrado.", e);
                    }
                    catch (JsonProcessingException e) {
                        throw new RuntimeException("Erro ao converter Json para JsonNode.");
                    }
                }).toList())
                .build();

        adEntity.getAdPublicationList().forEach(pub -> pub.setAd(adEntity));

        return adEntity;
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
                .mileage(source.getMileage())
                .build();
    }
}
