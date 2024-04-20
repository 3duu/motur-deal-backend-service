package br.com.motur.dealbackendservice.core.model;


import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.model.common.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Essa entidade representa um veículo.
 */
@Table(name = "vehicle")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @FieldMappingInfo(name = "id", type = DataType.ID)
    @ManyToOne
    @JoinColumn(name = "trim_id")
    private TrimEntity trim;

    @FieldMappingInfo(name = "modelYear", type = DataType.INT)
    @Column(name = "model_year")
    private Integer modelYear; // Ano do modelo

    @FieldMappingInfo(name = "productionYear", type = DataType.INT)
    @Column(name = "production_year")
    private Integer productionYear; // Ano de produção

    @FieldMappingInfo(name = "fuelId", type = DataType.INT)
    @Column(name = "fuel_id")
    @Enumerated(EnumType.ORDINAL)
    private FuelType fuelId; // Id do tipo de combustível

    @FieldMappingInfo(name = "transmissionType", type = DataType.STRING)
    @Enumerated(EnumType.ORDINAL)
    @Column(name="transmission_type")
    private TransmissionType transmissionType;

    @FieldMappingInfo(name = "licensePlate", type = DataType.STRING)
    @Column(name = "license_plate")
    private String licensePlate; // Placa

    @FieldMappingInfo(name = "color", type = DataType.INT)
    @Enumerated(EnumType.ORDINAL)
    private Color color; // Id da cor

    @FieldMappingInfo(name = "km", type = DataType.INT)
    @Column(name = "km")
    private Integer km; // Quilometragem

    @FieldMappingInfo(name = "price", type = DataType.DOUBLE)
    @Column(name = "price")
    private BigDecimal price; // Preço

    @FieldMappingInfo(name = "description", type = DataType.STRING)
    @Column(name = "description")
    private String description; // Descrição

    @FieldMappingInfo(name = "description", type = DataType.INT)
    @Column(name = "dealer_id")
    private Integer dealerId; // Id da concessionária

    @FieldMappingInfo(name = "dealerCityId", type = DataType.INT)
    @Column(name = "dealer_city_id")
    private Integer dealerCityId; // Id da cidade da concessionária

    @FieldMappingInfo(name = "dealerState", type = DataType.STRING)
    @Column(name = "dealer_state")
    private String dealerState; // Id do estado da concessionária

    @FieldMappingInfo(name = "status", type = DataType.STRING)
    @Column(name = "status")
    private String status; // Status

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "vehicle")
    private List<VehiclePublicationEntity> publication;

    /*@OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<VehicleEquipmentEntity> equipments;*/

    @ElementCollection
    @CollectionTable(name = "vehicle_providers", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "provider_id")
    private Set<Integer> providerIds;
}
