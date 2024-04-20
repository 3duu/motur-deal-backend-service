package br.com.motur.dealbackendservice.core.entrypoints.v1.request;


import br.com.motur.dealbackendservice.core.model.common.Color;
import br.com.motur.dealbackendservice.core.model.common.FuelType;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private Long id;

    private Integer providerId;

    private Integer brandId; // Id da marca

    private Integer modelId; // Id do modelo

    private Integer trimId; // Id da versão

    private Integer modelYear; // Ano do modelo

    private Integer productionYear; // Ano de produção

    private FuelType fuelType;  // Tipo de combustível

    private TransmissionType transmissionType;

    private String licensePlate; // Placa

    private Color color; // Id da cor

    private Integer km; // Quilometragem

    private BigDecimal price; // Preço

    private String description; // Descrição

    private Integer dealerId; // Id da concessionária

    private String status; // Status do anuncio

}
