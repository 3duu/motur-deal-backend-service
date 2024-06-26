package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Essa classe representa o um modelo de um veículo no catálogo do fornecedor.
 */
@Data
@Builder
@Entity
@Table(name = "provider_models")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProviderModelsEntity implements ProviderCatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FieldMappingInfo(name = "name", type = DataType.STRING)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_brand_id", nullable = false)
    private ProviderBrandsEntity parentProviderCatalog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_model_id", nullable = false)
    private ModelEntity baseModel;

    @FieldMappingInfo(name = "id", type = DataType.STRING)
    @Column(name = "external_id", length = 64, nullable = false)
    private String externalId;

    @Override
    public ProviderCatalogEntity getParentProviderCatalog() {
        return this.parentProviderCatalog;
    }

    @Override
    public void setParentProviderCatalog(ProviderCatalogEntity parentProviderCatalog) {
        this.parentProviderCatalog = (ProviderBrandsEntity) parentProviderCatalog;
    }

    @Override
    public void setBaseCatalog(CatalogEntity baseCatalog) {
        baseModel = (ModelEntity) baseCatalog;
    }


    @Override
    public String getCacheKey() {
        return "model:" + provider.getId() + ":" + parentProviderCatalog.getId() + ":" + baseModel.getId() + ":" + id;
    }
}
