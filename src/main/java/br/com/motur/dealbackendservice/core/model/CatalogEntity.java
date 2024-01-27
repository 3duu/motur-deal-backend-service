package br.com.motur.dealbackendservice.core.model;

/**
 * Essa classe representa uma entidade de catálogo local
 */
public interface CatalogEntity {

    String getName();

    void setName(String name);

    String[] getSynonymsArray();

    CatalogEntity getBaseParentCatalog();
}
