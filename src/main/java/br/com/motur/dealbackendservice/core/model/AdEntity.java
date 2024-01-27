package br.com.motur.dealbackendservice.core.model;


import br.com.motur.dealbackendservice.core.model.common.Color;
import br.com.motur.dealbackendservice.core.model.common.FuelType;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Essa entidade representa um anúncio local de um veículo.
 */
@Entity
@Data
@Table(name = "ad")
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderEntity provider;

    @Column(name = "brand_id")
    private Long brandId; // Id da marca

    @Column(name = "model_id")
    private Long modelId; // Id do modelo

    @Column(name = "trim_id")
    private Long trimId; // Id da versão

    @Column(name = "model_year")
    private Integer modelYear; // Ano do modelo

    @Column(name = "production_year")
    private Integer productionYear; // Ano de produção

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;  // Tipo de combustível

    @Column(name = "transmission_id")
    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @Column(name = "license_plate")
    private String licensePlate; // Placa

    @Column(name = "color_id")
    @Enumerated(EnumType.STRING)
    private Color color; // Id da cor

    @Column(name = "km")
    private Integer km; // Quilometragem

    @Column(name = "price")
    private BigDecimal price; // Preço

    @Column(name = "description")
    private String description; // Descrição

    @Column(name = "dealer_id")
    private Long dealerId; // Id da concessionária

    @Column(name = "dealer_city_id")
    private Long dealerCityId; // Id da cidade da concessionária

    @Column(name = "dealer_state_id")
    private Long dealerStateId; // Id do estado da concessionária

    @Column(name = "status")
    private String status; // Status do veículo


}
