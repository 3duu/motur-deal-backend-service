package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Essa classe representa o uma versão de uum veículo no catálogo do fornecedor.
 */

@Data
@Entity
@Table(name = "provider_trims")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProviderTrimsEntity implements ProviderCatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    @FieldMappingInfo(name = "name", type = DataType.STRING)
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
    @FieldMappingInfo(name = "id", type = DataType.STRING)
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
        return getProvider().getId() + ":" + externalId + ":" + (baseCatalog != null ? baseCatalog.getId() : StringUtils.EMPTY) + ":" + (parentProviderCatalog != null ? parentProviderCatalog.getId() : StringUtils.EMPTY);
    }
}

