package br.com.motur.dealbackendservice.core.finder;


import br.com.motur.dealbackendservice.core.model.CatalogEntity;

public interface CatalogFinder<T extends CatalogEntity> {

    boolean find(T providerCatalogEntity, String text);
}
