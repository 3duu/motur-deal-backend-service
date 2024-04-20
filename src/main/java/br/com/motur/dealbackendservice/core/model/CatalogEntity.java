package br.com.motur.dealbackendservice.core.model;

import java.io.Serializable;

/**
 * Essa classe representa uma entidade de catálogo local
 */
public interface CatalogEntity extends Serializable {

    Integer getId();

    String getName();

    void setName(String name);

    String[] getSynonymsArray();

    CatalogEntity getBaseParentCatalog();
}
