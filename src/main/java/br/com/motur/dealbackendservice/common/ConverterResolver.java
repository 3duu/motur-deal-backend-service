package br.com.motur.dealbackendservice.common;


public interface ConverterResolver {
    ValueObjectConverter getConverter(Class<?> to);

    ValueObjectConverter getConverter(Class<?> from, Class<?> to);
}
