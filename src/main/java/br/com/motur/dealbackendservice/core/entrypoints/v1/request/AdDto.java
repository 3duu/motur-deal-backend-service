package br.com.motur.dealbackendservice.core.entrypoints.v1.request;


import br.com.motur.dealbackendservice.core.model.common.Color;
import br.com.motur.dealbackendservice.core.model.common.FuelType;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Esse DTO representa um anúncio local de um veículo.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdDto implements Serializable {


    private Long id; // Id do anúncio

    @NotNull
    private Integer brandId; // Id da marca

    @NotNull
    private Integer modelId; // Id do modelo

    @NotNull
    private Integer trimId; // Id da versão

    @NotNull
    private Integer modelYear; // Ano do modelo

    @NotNull
    private Integer productionYear; // Ano de produção

    @NotNull
    private FuelType fuelType;  // Tipo de combustível

    @NotNull
    private TransmissionType transmissionType;

    @NotNull
    private String licensePlate; // Placa

    @NotNull
    private Color color; // Id da cor

    @NotNull
    private Integer km; // Quilometragem

    @NotNull
    private BigDecimal price; // Preço

    private String description; // Descrição

    @NotNull
    private Integer dealerId; // Id da concessionária

    private String status; // Status do anuncio

    private Integer mileage; // Quilometragem

}
