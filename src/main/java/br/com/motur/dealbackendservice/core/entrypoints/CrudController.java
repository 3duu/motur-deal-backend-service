package br.com.motur.dealbackendservice.core.entrypoints;


import br.com.motur.dealbackendservice.common.ConverterResolver;
import br.com.motur.dealbackendservice.common.ValueObjectConverter;
import br.com.motur.dealbackendservice.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Classe base para os controllers
 * @param <T> Objeto de entrada
 */
@RestController
public abstract class CrudController<T> {

    @Autowired
    private ConverterResolver converterResolver;

    private ValueObjectConverter converter;

    public static List checkListContent(final List list) {
        return (List) ControllerUtils.checkContent(list);
    }

    @EventListener
    private void onConstruct(final ApplicationReadyEvent event) {
        final Type[] persistentClass = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        converter = converterResolver.getConverter((Class) persistentClass[0]);
    }

    protected ConverterResolver getConverterResolver() {
        return converterResolver;
    }

    public final ValueObjectConverter getConverter() {
        return converter;
    }
}
