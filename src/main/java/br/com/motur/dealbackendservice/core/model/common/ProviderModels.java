package br.com.motur.dealbackendservice.core.model.common;

import br.com.motur.dealbackendservice.core.model.ModelEntity;
import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "provider_models")
public class ProviderModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_brand_id", nullable = false)
    private ProviderBrands brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_model_id", nullable = false)
    private ModelEntity baseModel;

}
