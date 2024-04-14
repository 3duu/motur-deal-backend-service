package br.com.motur.dealbackendservice.core.model.common;

import java.io.Serializable;

public interface CacheableEntity extends Serializable {

    String getCacheKey();
}
