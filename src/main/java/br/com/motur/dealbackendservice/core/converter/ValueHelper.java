package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.model.AdEntity;

public interface ValueHelper<T,V> {

    V getValue(T object);

    boolean isNull(T object);

    V getDefaultValue(T adEntity, FieldMappingInfo fieldMappingInfo);
}
