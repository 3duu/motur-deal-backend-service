package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.CacheableEntity;

/**
 * Essa interface representa um item do catálogo de um fornecedor. Ex: Modelo, Versão, Marca, etc.
 */
public interface ProviderCatalogEntity extends CacheableEntity {

    String getExternalId();

    void setExternalId(String externalId);

    String getName();

    void setName(String name);

    ProviderEntity getProvider();

    void setProvider(ProviderEntity provider);

    ProviderCatalogEntity getParentProviderCatalog();

    void setParentProviderCatalog(ProviderCatalogEntity parentProviderCatalog);

    void setBaseCatalog(CatalogEntity baseCatalog);

}
