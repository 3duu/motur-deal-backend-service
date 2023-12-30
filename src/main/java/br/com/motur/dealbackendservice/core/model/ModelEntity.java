package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "model")
public class ModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer modelId;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    private String name;
}
