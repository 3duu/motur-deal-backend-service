package br.com.motur.dealbackendservice.core.service.vo;


import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.converter.VoTitleHelper;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.Color;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import br.com.motur.dealbackendservice.core.model.common.FuelType;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Builder
@AllArgsConstructor
@Table(name = "ad")
public class AdVo implements Serializable {

    private Long id;

    private TrimEntity baseTrim;
    private ModelEntity baseModel;
    private BrandEntity baseBrand;

    @FieldMappingInfo(name = "modelYear", type = DataType.INT)
    private Integer modelYear; // Ano do modelo

    @FieldMappingInfo(name = "productionYear", type = DataType.INT)
    private Integer productionYear; // Ano de produção

    @FieldMappingInfo(name = "fuelId", type = DataType.INT)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;  // Tipo de combustível

    @FieldMappingInfo(name = "transmissionType", type = DataType.STRING)
    @Column(name = "transmission_id")
    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @FieldMappingInfo(name = "licensePlate", type = DataType.STRING)
    private String licensePlate; // Placa

    @FieldMappingInfo(name = "color", type = DataType.INT)
    @Enumerated(EnumType.STRING)
    private Color color; // Id da cor

    @FieldMappingInfo(name = "km", type = DataType.INT)
    private Integer km; // Quilometragem

    @FieldMappingInfo(name = "price", type = DataType.DOUBLE)
    private BigDecimal price; // Preço

    @FieldMappingInfo(name = "mileage", type = DataType.INT)
    private Integer mileage; // Quilometragem

    @FieldMappingInfo(name = "description", type = DataType.STRING)
    private String description; // Descrição

    @FieldMappingInfo(name = "tile", type = DataType.STRING, helper = VoTitleHelper.class)
    private String title; // Descrição

    @FieldMappingInfo(name = "dealer", type = DataType.ID)
    private DealerEntity dealer; // Id da concessionária

    //@Column(name = "status")
    private String status; // Status do veículo

    private ProviderEntity provider;

    @FieldMappingInfo(name = "brand", type = DataType.ID)
    private ProviderBrandsEntity providerBrands;

    @FieldMappingInfo(name = "model", type = DataType.ID)
    private ProviderModelsEntity providerModels;

    @FieldMappingInfo(name = "trim", type = DataType.ID)
    private ProviderTrimsEntity providerTrims;

    /*@Getter
    @Builder
    @AllArgsConstructor
    public static class AdPublicationVo {
        private Long id;
        private String externalId;
        private Long providerTrimId;
        private String planId;
        private ProviderEntity provider;
        private Map<String,Object> additionalInfo;
    }*/

}
