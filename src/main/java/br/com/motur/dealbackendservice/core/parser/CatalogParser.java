package br.com.motur.dealbackendservice.core.parser;


import br.com.motur.dealbackendservice.core.model.CatalogEntity;
import br.com.motur.dealbackendservice.core.model.ProviderCatalogEntity;

import java.util.List;

public interface CatalogParser<T extends CatalogEntity, E extends List<ProviderCatalogEntity>> {

    ProviderCatalogEntity parse(E providerCatalogEntity, List<ProviderCatalogEntity> providerCatalogEntities);
}
