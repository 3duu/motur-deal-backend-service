package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Essa classe representa o um modelo de um veículo no catálogo do fornecedor.
 */
@Data
@Entity
@Table(name = "provider_models")
public class ProviderModels implements ProviderCatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_brand_id", nullable = false)
    private ProviderBrands parentProviderCatalog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_model_id", nullable = false)
    private ModelEntity baseModel;

    @Column(name = "external_id", length = 64, nullable = false)
    private String externalId;

    @Override
    public ProviderCatalogEntity getParentProviderCatalog() {
        return this.parentProviderCatalog;
    }

    @Override
    public void setParentProviderCatalog(ProviderCatalogEntity parentProviderCatalog) {
        this.parentProviderCatalog = (ProviderBrands) parentProviderCatalog;
    }

    @Override
    public void setBaseCatalog(CatalogEntity baseCatalog) {
        baseModel = (ModelEntity) baseCatalog;
    }


}
