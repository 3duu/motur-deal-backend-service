package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "PlanMapping")
public class PlanMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planMappingId;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @Column
    private String localPlanField;

    @Column
    private String externalPlanField;

}
