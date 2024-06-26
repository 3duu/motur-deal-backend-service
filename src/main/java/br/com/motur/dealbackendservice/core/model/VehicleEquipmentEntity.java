package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "vehicle_equipment")
public class VehicleEquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer equipmentId;
/*
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;*/

    private String name;
    private String description;
}
