package br.com.motur.dealbackendservice.core.model;


import br.com.motur.dealbackendservice.core.model.common.BodyType;
import br.com.motur.dealbackendservice.core.model.common.Color;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Table(name = "vehicle")
@Entity
@Data
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trim_id")
    private TrimEntity trim;

    @Column(name = "model_year")
    private Integer modelYear; // Ano do modelo

    @Column(name = "production_year")
    private Integer productionYear; // Ano de produção

    @Enumerated(EnumType.ORDINAL)
    private Long fuelId; // Id do tipo de combustível

    @Enumerated(EnumType.ORDINAL)
    private TransmissionType transmissionType;

    @Column(name = "license_plate")
    private String licensePlate; // Placa

    @Enumerated(EnumType.ORDINAL)
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
    private String status; // Status

    /*@Column(name = "provider_id")
    private Long providerId; // Id do fornecedor*/

    @Column(name = "vehicle_type")
    private String vehicleType; // Tipo do veículo

    @Column(name = "body_type")
    @Enumerated(EnumType.ORDINAL)
    private BodyType bodyType;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<VehicleEquipmentEntity> equipments;

    @ElementCollection
    @CollectionTable(name = "vehicle_providers", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "provider_id")
    private Set<Long> providerIds;
}
