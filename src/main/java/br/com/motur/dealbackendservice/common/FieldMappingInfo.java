package br.com.motur.dealbackendservice.common;

import br.com.motur.dealbackendservice.core.model.common.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Essa anotação é responsável por mapear os campos do arquivo de catálogo para os campos da entidade
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldMappingInfo {
    String name();
    DataType type() default DataType.STRING;
}
