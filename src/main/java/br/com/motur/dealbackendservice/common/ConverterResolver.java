package br.com.motur.dealbackendservice.common;


/**
 * Essa anotação é responsável por mapear os campos do arquivo de catálogo para os campos da entidade
 */
public interface ConverterResolver {
    ValueObjectConverter getConverter(Class<?> to);

    ValueObjectConverter getConverter(Class<?> from, Class<?> to);
}
