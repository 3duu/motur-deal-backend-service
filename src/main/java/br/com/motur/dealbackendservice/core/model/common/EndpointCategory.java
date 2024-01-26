package br.com.motur.dealbackendservice.core.model.common;

import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import br.com.motur.dealbackendservice.core.model.ProviderCatalogEntity;
import br.com.motur.dealbackendservice.core.model.ProviderModels;
import br.com.motur.dealbackendservice.core.model.ProviderTrims;
import br.com.motur.dealbackendservice.core.parser.CatalogParser;
import lombok.Getter;

@Getter
public enum EndpointCategory {
    AUTHENTICATION("Autenticação", null, CatalogParser.class),
    CATALOG_BRANDS("Catálogo de Marcas", ProviderBrands.class, CatalogParser.class),
    CATALOG_MODELS("Catálogo de Modelos", ProviderModels.class, CatalogParser.class),
    CATALOG_VERSIONS("Catálogo de Versões", ProviderTrims.class, CatalogParser.class),
    LISTING("Listagem", null, CatalogParser.class),
    DETAILS("Detalhes", null, CatalogParser.class),
    CREATION("Criação", null, CatalogParser.class),
    UPDATE("Atualização", null, CatalogParser.class),
    DELETION("Deleção", null, CatalogParser.class),
    STATUS("Status", null, CatalogParser.class),
    STATISTICS("Estatísticas", null, CatalogParser.class),
    OTHER("Outro", null, CatalogParser.class);

    private final String displayName;
    private final Class<? extends ProviderCatalogEntity> entityClass;
    private final Class<? extends CatalogParser> parseClass;

    EndpointCategory(final String displayName, final Class<? extends ProviderCatalogEntity> entityClass, Class<? extends CatalogParser> parseClass) {
        this.displayName = displayName;
        this.entityClass = entityClass;
        this.parseClass = parseClass;
    }

}

