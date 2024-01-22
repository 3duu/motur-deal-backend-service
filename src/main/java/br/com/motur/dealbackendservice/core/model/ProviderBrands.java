package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "provider_brands")
public class ProviderBrands {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "external_id", length = 64, nullable = false)
    private String externalId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_brand_id", nullable = false)
    private BrandEntity baseBrand;

}