package br.com.motur.dealbackendservice.core.model.common;

import br.com.motur.dealbackendservice.core.finder.ModelsFinder;
import br.com.motur.dealbackendservice.core.finder.TrimsFinder;
import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import br.com.motur.dealbackendservice.core.model.ProviderCatalogEntity;
import br.com.motur.dealbackendservice.core.model.ProviderModelsEntity;
import br.com.motur.dealbackendservice.core.model.ProviderTrims;
import br.com.motur.dealbackendservice.core.finder.BrandsFinder;
import br.com.motur.dealbackendservice.core.finder.CatalogFinder;
import lombok.Getter;

@Getter
public enum EndpointCategory {
    AUTHENTICATION("Autenticação", null, null),
    CATALOG_BRANDS("Catálogo de Marcas", ProviderBrands.class, new BrandsFinder()),
    CATALOG_MODELS("Catálogo de Modelos", ProviderModelsEntity.class, new ModelsFinder()),
    CATALOG_TRIMS("Catálogo de Versões", ProviderTrims.class, new TrimsFinder()),
    CATALOG_COLORS("Catálogo de Cores", null, null),
    CATALOG_TRANSMISSIONS("Catálogo de Transmissões", null, null),
    CATALOG_FUEL("Catálogo de Combustíveis", null, null),
    CATALOG_BODY_TYPES("Catálogo de Tipos de Carroceria", null, null),
    CATALOG_CATEGORIES("Catálogo de Categorias", null, null),
    CATALOG_FEATURES("Catálogo de Características", null, null),
    CATALOG_EQUIPMENTS("Catálogo de Equipamentos", null, null),
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

