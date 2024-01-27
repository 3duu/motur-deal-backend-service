package br.com.motur.dealbackendservice.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe abstrata para conversão de objetos
 * @param <T> Tipo de objeto de origem
 * @param <E> Tipo de objeto de destino
 */
public abstract class ValueObjectConverter<T, E> implements Converter<T, E> {

    public List<E> convertList(List<T> list){
        return list.stream().map(m -> convert(m)).collect(Collectors.toList());
    }

    public List<E> convertEnum() throws Exception {
        try {
            final Type[] persistentClass = ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments();
            if (persistentClass.length > 0) {
                final Type type = persistentClass[0];
                if(type instanceof Class && ((Class<?>)type).isEnum()){
                    Method method = ((Class<?>)type).getDeclaredMethod("values");
                    Object obj = method.invoke(null);
                    if (obj != null){
                        return Arrays.stream(((Object[]) obj)).map(m -> convert((T)m)).collect(Collectors.toList());
                    }
                }
                else {
                    throw new Exception("Erro ao converter instancia. Classe de origem não é do tipo enum");
                }
            }
        }
        catch (Exception e) {
            throw e;
        }

        return new ArrayList<>();
    }

    @Nullable
    public abstract T invert(E source);
}
