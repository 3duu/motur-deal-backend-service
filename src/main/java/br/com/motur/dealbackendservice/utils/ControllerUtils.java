package br.com.motur.dealbackendservice.utils;

import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException;
import org.slf4j.Logger;

import java.util.Collection;

public class ControllerUtils {

    private final static String message = "Conteúdo não encontrado";

    private ControllerUtils(){

    }

    public static Collection checkContent(final Collection list, final Logger logger) throws ResourceNotFoundException {
        if (list == null || list.isEmpty())
            throw new ResourceNotFoundException(message);

        return list;
    }

    public static Object checkContent(final Object object, final Logger logger) throws ResourceNotFoundException{
        if (object == null || object.toString().trim().isEmpty())
            throw new ResourceNotFoundException(message);

        return object;
    }

    public static Collection checkContent(final Collection list) throws ResourceNotFoundException{
        if (list == null || list.isEmpty())
            throw new ResourceNotFoundException(message);

        return list;
    }

    public static Object checkContent(final Object object) throws ResourceNotFoundException{
        if (object == null || object.toString().trim().isEmpty())
            throw new ResourceNotFoundException(message);

        return object;
    }
}
