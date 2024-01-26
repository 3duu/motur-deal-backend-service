package br.com.motur.dealbackendservice.core.model.common;

import br.com.motur.dealbackendservice.core.finder.ModelsFinder;
import br.com.motur.dealbackendservice.core.finder.TrimsFinder;
import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import br.com.motur.dealbackendservice.core.model.ProviderCatalogEntity;
import br.com.motur.dealbackendservice.core.model.ProviderModels;
import br.com.motur.dealbackendservice.core.model.ProviderTrims;
import br.com.motur.dealbackendservice.core.finder.BrandsFinder;
import br.com.motur.dealbackendservice.core.finder.CatalogFinder;
import lombok.Getter;

@Getter
public enum EndpointCategory {
    AUTHENTICATION("Autenticação", null, null),
    CATALOG_BRANDS("Catálogo de Marcas", ProviderBrands.class, new BrandsFinder()),
    CATALOG_MODELS("Catálogo de Modelos", ProviderModels.class, new ModelsFinder()),
    CATALOG_TRIMS("Catálogo de Versões", ProviderTrims.class, new TrimsFinder()),
    LISTING("Listagem", null, null),
    DETAILS("Detalhes", null, null),
    CREATION("Criação", null, null),
    UPDATE("Atualização", null, null),
    DELETION("Deleção", null, null),
    STATUS("Status", null, null),
    STATISTICS("Estatísticas", null, null),
    OTHER("Outro", null, null);

    private final String displayName;
    private final Class<? extends ProviderCatalogEntity> entityClass;
    private final CatalogFinder finderInstance;

    EndpointCategory(final String displayName, final Class<? extends ProviderCatalogEntity> entityClass, final CatalogFinder finderInstance) {
        this.displayName = displayName;
        this.entityClass = entityClass;
        this.finderInstance = finderInstance;
    }

}

