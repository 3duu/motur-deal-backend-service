package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Essa classe representa o uma versão de uum veículo no catálogo do fornecedor.
 */

@Data
@Entity
@Table(name = "provider_trims")
public class ProviderTrimsMinimalEntity implements ProviderCatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "provider_id", nullable = false)
    private Integer providerId;

    @Column(name = "provider_model_id", nullable = false)
    private Long parentProviderCatalogId;

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
    public ProviderEntity getProvider() {
        return ProviderEntity.builder().id(providerId).build();
    }

    @Override
    public void setProvider(ProviderEntity provider) {

    }

    @Override
    public ProviderCatalogEntity getParentProviderCatalog() {
        return ProviderModelsEntity.builder().id(parentProviderCatalogId).build();
    }

    @Override
    public void setParentProviderCatalog(ProviderCatalogEntity parentProviderCatalog) {

    }

    @Override
    public String getCacheKey() {
        return getProvider().getId() + ":" + externalId + ":" + (baseCatalog != null ? baseCatalog.getId() : StringUtils.EMPTY) + ":" + parentProviderCatalogId;
    }
}

