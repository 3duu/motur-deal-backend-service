package br.com.motur.dealbackendservice.core.entrypoints.v1.request;

import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.model.VehiclePublicationEntity;
import br.com.motur.dealbackendservice.core.model.common.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDTO implements Serializable {

    private Long id;

    @NotNull
    @FieldMappingInfo(name = "id", type = DataType.INT)
    private Integer trimId;

    @NotNull
    @FieldMappingInfo(name = "modelYear", type = DataType.INT)
    private Integer modelYear; // Ano do modelo

    @NotNull
    @FieldMappingInfo(name = "productionYear", type = DataType.INT)
    private Integer productionYear; // Ano de produção

    @NotNull
    @FieldMappingInfo(name = "fuelId", type = DataType.INT)
    private FuelType fuelId; // Id do tipo de combustível

    @NotNull
    @FieldMappingInfo(name = "transmissionType", type = DataType.STRING)
    private TransmissionType transmissionType;

    @FieldMappingInfo(name = "licensePlate", type = DataType.STRING)
    private String licensePlate; // Placa

    @NotNull
    @FieldMappingInfo(name = "color", type = DataType.INT)
    private Color color; // Id da cor

    @NotNull
    @FieldMappingInfo(name = "km", type = DataType.INT)
    private Integer km; // Quilometragem

    @NotNull
    @FieldMappingInfo(name = "price", type = DataType.DOUBLE)
    private Float price; // Preço

    @FieldMappingInfo(name = "description", type = DataType.STRING)
    private String description; // Descrição

    @NotNull
    @FieldMappingInfo(name = "description", type = DataType.INT)
    private Integer dealerId; // Id da concessionária

    @NotNull
    @FieldMappingInfo(name = "dealerCityId", type = DataType.INT)
    @Column(name = "dealer_city_id")
    private Integer dealerCityId; // Id da cidade da concessionária

    @NotNull
    @FieldMappingInfo(name = "dealerState", type = DataType.STRING)
    private String dealerState; // Id do estado da concessionária

    @NotNull
    @FieldMappingInfo(name = "status", type = DataType.STRING)
    private String status; // Status

    @NotNull
    private List<VehiclePublicationEntity> publication;

    @NotNull
    @Column(name = "provider_id")
    private Set<Long> providerIds;
}
