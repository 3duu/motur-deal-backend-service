package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.Column;
import lombok.Data;

public abstract class BaseProviderCatalogEntity {

    public abstract String getExternalId();

    public abstract void setExternalId(String externalId);
}
