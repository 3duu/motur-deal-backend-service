package br.com.motur.dealbackendservice.core.model.common;

public enum EndpointCategory {
    AUTHENTICATION("Autenticação"),
    CATALOG_BRANDS("Catálogo de Marcas"),
    CATALOG_MODELS("Catálogo de Modelos"),
    CATALOG_VERSIONS("Catálogo de Versões"),
    LISTING("Listagem"),
    DETAILS("Detalhes"),
    CREATION("Criação"),
    UPDATE("Atualização"),
    DELETION("Deleção"),
    STATUS("Status"),
    STATISTICS("Estatísticas"),
    OTHER("Outro");

    private final String displayName;

    EndpointCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

