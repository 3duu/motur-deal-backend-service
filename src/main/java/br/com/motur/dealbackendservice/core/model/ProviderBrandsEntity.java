package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Essa classe representa o uma marca de um veículo no catálogo do fornecedor.
 */
@Data
@Entity
@Table(name = "provider_brands")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProviderBrandsEntity implements ProviderCatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 50, nullable = false)
    @FieldMappingInfo(name = "name", type = DataType.STRING)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_brand_id", nullable = false)
    private BrandEntity baseCatalog;

    @FieldMappingInfo(name = "id", type = DataType.STRING)
    @Column(name = "external_id", length = 64, nullable = false)
    private String externalId;

    @Override
    public ProviderCatalogEntity getParentProviderCatalog() {
        return null;
    }

    @Override
    public void setParentProviderCatalog(final ProviderCatalogEntity parentProviderCatalog) {

    }

    @Override
    public void setBaseCatalog(CatalogEntity baseCatalog) {
        this.baseCatalog = (BrandEntity) baseCatalog;
    }


    @Override
    public String getCacheKey() {
        return provider.getId() + ":" + baseCatalog.getId() + ":" + id;
    }
}
