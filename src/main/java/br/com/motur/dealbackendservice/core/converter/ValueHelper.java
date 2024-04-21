package br.com.motur.dealbackendservice.core.converter;

public interface ValueHelper<T,V> {

    V getValue(T object);

    V getDefaultValue(T object);

    boolean isNull(T object);
}
