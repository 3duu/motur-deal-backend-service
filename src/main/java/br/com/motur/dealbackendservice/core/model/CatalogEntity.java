package br.com.motur.dealbackendservice.core.model;

/**
 * Essa classe representa uma entidade de catálogo local
 */
public interface CatalogEntity {

    Integer getId();

    String getName();

    void setName(String name);

    String[] getSynonymsArray();

    CatalogEntity getBaseParentCatalog();
}
