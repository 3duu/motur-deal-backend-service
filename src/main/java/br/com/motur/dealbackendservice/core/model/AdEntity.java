package br.com.motur.dealbackendservice.core.model;


import br.com.motur.dealbackendservice.core.model.common.Color;
import br.com.motur.dealbackendservice.core.model.common.FuelType;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Essa entidade representa um anúncio local de um veículo.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ad")
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderEntity provider;

    @Column(name = "brand_id")
    private Integer brandId; // Id da marca

    @Column(name = "model_id")
    private Integer modelId; // Id do modelo

    @Column(name = "trim_id")
    private Integer trimId; // Id da versão

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private DealerEntity dealer; // Id da concessionária

    @Column(name = "status")
    private String status; // Status do veículo

}
