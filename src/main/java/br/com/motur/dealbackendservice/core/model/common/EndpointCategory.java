package br.com.motur.dealbackendservice.core.model.common;

import br.com.motur.dealbackendservice.core.model.BaseProviderCatalogEntity;
import br.com.motur.dealbackendservice.core.model.ProviderModels;
import br.com.motur.dealbackendservice.core.model.ProviderTrims;

public enum EndpointCategory {
    AUTHENTICATION("Autenticação", null),
    CATALOG_BRANDS("Catálogo de Marcas", BaseProviderCatalogEntity.class),
    CATALOG_MODELS("Catálogo de Modelos", ProviderModels.class),
    CATALOG_VERSIONS("Catálogo de Versões", ProviderTrims.class),
    LISTING("Listagem", null),
    DETAILS("Detalhes", null),
    CREATION("Criação", null),
    UPDATE("Atualização", null),
    DELETION("Deleção", null),
    STATUS("Status", null),
    STATISTICS("Estatísticas", null),
    OTHER("Outro", null);

    private final String displayName;
    private final Class<?> clazz;

    EndpointCategory(final String displayName, final Class<?> clazz) {
        this.displayName = displayName;
        this.clazz = clazz;
    }

    public String getDisplayName() {
        return displayName;
    }
}

