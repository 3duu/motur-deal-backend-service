package br.com.motur.dealbackendservice.core.model;

import lombok.Data;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;

/**
 * Essa classe representa o uma versão de uum veículo no catálogo do fornecedor.
 */

@Data
@Entity
@Table(name = "provider_trims")
public class ProviderTrims implements ProviderCatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_model_id", nullable = false)
    private ProviderModelsEntity parentProviderCatalog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_trim_id", nullable = false)
    private TrimEntity baseCatalog;

    @Column(name = "external_id", length = 64, nullable = false)
    private String externalId;

    @Override
    public void setBaseCatalog(CatalogEntity baseCatalog) {
        this.baseCatalog = (TrimEntity) baseCatalog;
    }

    @Override
    public ProviderCatalogEntity getParentProviderCatalog() {
        return this.parentProviderCatalog;
    }

    @Override
    public void setParentProviderCatalog(ProviderCatalogEntity parentProviderCatalog) {
        this.parentProviderCatalog = (ProviderModelsEntity) parentProviderCatalog;
    }

    @Override
    public String getCacheKey() {
        return STR."\{getProvider().getId()}:\{getExternalId()}:\{getBaseCatalog() != null ? getBaseCatalog().getId() : StringUtils.EMPTY}:\{getParentProviderCatalog() != null ? getParentProviderCatalog().getExternalId() : StringUtils.EMPTY}";
    }
}

