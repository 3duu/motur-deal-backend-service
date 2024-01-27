package br.com.motur.dealbackendservice.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Essa classe é responsável por resolver o conversor de acordo com o tipo de objeto
 */
@Service
public class ConverterResolverImpl implements ConverterResolver {

    @Autowired
    private Map<String, ValueObjectConverter> services;

    @Override
    public ValueObjectConverter getConverter(Class<?> to) {

        for (var k : services.keySet()) {
            final Type[] persistentClass = ((ParameterizedType)services.get(k).getClass().getGenericSuperclass()).getActualTypeArguments();
            if (persistentClass.length == 2) {
                if (((Class)persistentClass[1]).getName().equals(to.getName())){
                    return services.get(k);
                }
            }
        }

        return null;
    }

    @Override
    public ValueObjectConverter getConverter(Class<?> from, Class<?> to) {

        for (var k : services.keySet()) {
            final Type[] persistentClass = ((ParameterizedType)services.get(k).getClass().getGenericSuperclass()).getActualTypeArguments();
            if (persistentClass.length == 2) {
                if (((Class)persistentClass[0]).getName().equals(from.getName()) && ((Class)persistentClass[1]).getName().equals(to.getName())){
                    return services.get(k);
                }
            }
        }

        return null;
    }
}
