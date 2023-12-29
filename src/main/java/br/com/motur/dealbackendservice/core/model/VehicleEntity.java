package br.com.motur.dealbackendservice.core.model;


import br.com.motur.dealbackendservice.core.model.common.BodyType;
import br.com.motur.dealbackendservice.core.model.common.Color;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long brandId; // Id da marca
    private Long modelId; // Id do modelo
    private Long trimId; // Id da versão
    private Integer modelYear; // Ano do modelo
    private Integer productionYear; // Ano de produção
    private Long fuelId; // Id do tipo de combustível
    @Enumerated(EnumType.ORDINAL)
    private TransmissionType transmissionType;
    private String licensePlate; // Placa
    @Enumerated(EnumType.ORDINAL)
    private Color color; // Id da cor
    private Integer km; // Quilometragem
    private BigDecimal price; // Preço
    private String description; // Descrição
    private Long dealerId; // Id da concessionária
    private Long dealerCityId; // Id da cidade da concessionária
    private Long dealerStateId; // Id do estado da concessionária
    private String status; // Status
    private Long providerId; // Id do fornecedor
    private String vehicleType; // Tipo do veículo
    @Enumerated(EnumType.ORDINAL)
    private BodyType bodyType;
}
