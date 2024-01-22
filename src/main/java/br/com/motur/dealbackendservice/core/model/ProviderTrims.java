package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.ProviderModels;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "provider_trims")
public class ProviderTrims {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "external_id", length = 64, nullable = false)
    private String externalId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_model_id", nullable = false)
    private ProviderModels model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_trim_id", nullable = false)
    private TrimEntity baseTrim;

}

