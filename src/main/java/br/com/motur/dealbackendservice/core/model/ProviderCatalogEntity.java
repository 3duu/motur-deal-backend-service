package br.com.motur.dealbackendservice.core.model;


import java.io.Serializable;

public interface ProviderCatalogEntity extends Serializable {

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
