package br.com.motur.dealbackendservice.core.converter;

public interface ValueHelper<T,V> {

    V getValue(T object);

    boolean isNull(T object);

    V getDefaultValue(T adEntity, final Object reference);
}
