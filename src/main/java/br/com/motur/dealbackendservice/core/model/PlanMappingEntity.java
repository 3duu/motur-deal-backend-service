package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "plan_mapping")
public class PlanMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @Column(nullable = false, name = "local_plan_field", length = 30)
    private String localPlanField;

    @Column(nullable = false, name = "external_plan_field", length = 30)
    private String externalPlanField;

}
