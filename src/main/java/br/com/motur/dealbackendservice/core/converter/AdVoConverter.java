package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.common.ValueObjectConverter;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.service.BrandService;
import br.com.motur.dealbackendservice.core.service.ModelService;
import br.com.motur.dealbackendservice.core.service.TrimService;
import br.com.motur.dealbackendservice.core.service.vo.AdVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class AdVoConverter extends ValueObjectConverter<AdEntity, AdVo> {

    private final TrimService trimService;
    private final ProviderRepository providerRepository;
    private final ObjectMapper objectMapper;
    private final BrandService brandService;
    private final ModelService modelService;

    @Autowired
    public AdVoConverter(TrimService trimService, ProviderRepository providerRepository, ObjectMapper objectMapper, BrandService brandService, ModelService modelService) {
        this.trimService = trimService;
        this.providerRepository = providerRepository;
        this.objectMapper = objectMapper;
        this.brandService = brandService;
        this.modelService = modelService;
    }


    @Override
    public AdEntity invert(AdVo source) {
        return AdEntity.builder().id(source.getId())
                .brandId(source.getBaseBrand().getId())
                .modelId(source.getBaseModel().getId())
                .trimId(source.getBaseTrim().getId())
                .price(source.getPrice())
                .color(source.getColor())
                .description(source.getDescription())
                .modelYear(source.getModelYear())
                .productionYear(source.getProductionYear())
                .fuelType(source.getFuelType())
                .transmissionType(source.getTransmissionType())
                .licensePlate(source.getLicensePlate())
                .status(source.getStatus())
                .mileage(source.getMileage())
                .dealer(source.getDealer())

                .build();
    }

    @Override
    public AdVo convert(AdEntity source) {

        TrimEntity trim = trimService.findById(source.getTrimId());

        return AdVo.builder().id(source.getId())
                .baseBrand(source.getBrandId() != null ? brandService.findById(source.getBrandId()) : trim.getModel().getBrand())
                .baseModel(source.getModelId() != null ? modelService.findById(source.getModelId()) : trim.getModel())
                .baseTrim(trim)
                .price(source.getPrice())
                .color(source.getColor())
                .description(source.getDescription())
                .modelYear(source.getModelYear())
                .productionYear(source.getProductionYear())
                .fuelType(source.getFuelType())
                .transmissionType(source.getTransmissionType())
                .licensePlate(source.getLicensePlate())
                .status(source.getStatus())
                .mileage(source.getMileage())
                .dealer(source.getDealer())
                //.provider(source.getProvider())
                .km(source.getKm())
                //.providerTrims(source.getAdPublicationList().stream().map(AdPublicationEntity::getProviderTrimsEntity).collect(Collectors.toList()))
                .build();
    }

    public AdVo convert(final AdEntity source, final ProviderEntity provider) {

        final TrimEntity trim = trimService.findById(source.getTrimId());
        final AdPublicationEntity providerPub = source.getAdPublicationList().stream().filter(t -> t.getProvider().getId().equals(provider.getId())).findAny().orElse(null);

        return AdVo.builder().id(source.getId())
                .baseBrand(source.getBrandId() != null ? brandService.findById(source.getBrandId()) : trim.getModel().getBrand())
                .baseModel(source.getModelId() != null ? modelService.findById(source.getModelId()) : trim.getModel())
                .baseTrim(trim)
                .price(source.getPrice())
                .color(source.getColor())
                .description(source.getDescription())
                .modelYear(source.getModelYear())
                .productionYear(source.getProductionYear())
                .fuelType(source.getFuelType())
                .transmissionType(source.getTransmissionType())
                .licensePlate(source.getLicensePlate())
                .status(source.getStatus())
                .mileage(source.getMileage())
                .dealer(source.getDealer())
                .provider(provider)
                .km(source.getKm())
                .providerTrims(providerPub.getProviderTrimsEntity())
                .providerModels((ProviderModelsEntity) providerPub.getProviderTrimsEntity().getParentProviderCatalog())
                .providerBrands((ProviderBrandsEntity) providerPub.getProviderTrimsEntity().getParentProviderCatalog().getParentProviderCatalog())
                .build();
    }

    @Override
    public <U> Converter<AdEntity, U> andThen(Converter<? super AdVo, ? extends U> after) {
        return super.andThen(after);
    }
}
