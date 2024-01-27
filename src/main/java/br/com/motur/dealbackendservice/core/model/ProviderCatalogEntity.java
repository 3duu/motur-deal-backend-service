package br.com.motur.dealbackendservice.core.model;


import java.io.Serializable;

/**
 * Essa interface representa um item do catálogo de um fornecedor. Ex: Modelo, Versão, Marca, etc.
 */
public interface ProviderCatalogEntity extends Serializable {

    String getExternalId();

    void setExternalId(String externalId);

    String getName();

    void setName(String name);

    ProviderEntity getProvider();

    void setProvider(ProviderEntity provider);

    /**
     * Retorna o item pai desse item no catálogo do fornecedor. No caso da Marca, esse método retorna null.
     * @return
     */
    ProviderCatalogEntity getParentProviderCatalog();

    void setParentProviderCatalog(ProviderCatalogEntity parentProviderCatalog);

    void setBaseCatalog(CatalogEntity baseCatalog);
}
