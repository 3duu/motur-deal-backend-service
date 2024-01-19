package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.BodyType;
import br.com.motur.dealbackendservice.core.model.common.FuelType;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "trim")
public class TrimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private ModelEntity model;

    private String name;

    @Column(name = "year_from")
    private Integer yearFrom;

    @Column(name = "year_to")
    private Integer yearTo;

    @Column(name = "qt_doors")
    private Short qtoors; // Número de portas

    @Column(name = "code_a")
    private String codaA;

    @Column(name = "engine_hp")
    private Integer engineHp;

    @Column(name = "torque")
    private Float torque;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "traction")
    private String traction;

    @Column(name = "vehicle_type")
    private String vehicleType; // Tipo do veículo

    @Column(name = "body_type")
    @Enumerated(EnumType.ORDINAL)
    private BodyType bodyType;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;  // Tipo de combustível

    @Column(name = "transmission_id")
    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;
}
