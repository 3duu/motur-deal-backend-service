package br.com.motur.dealbackendservice.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Service
public class DynamicRepositoryService {

    @Autowired
    private ApplicationContext context;

    public <T, ID> JpaRepository<T, ID> getRepositoryForEntity(Class<T> entityType) {
        // Getting the repository bean by type
        String[] beanNames = context.getBeanNamesForType(JpaRepository.class);
        for (String beanName : beanNames) {
            JpaRepository repository = context.getBean(beanName, JpaRepository.class);
            if (isRepositoryForEntity(repository, entityType)) {
                return repository;
            }
        }
        throw new IllegalArgumentException("No repository found for entity type: " + entityType.getSimpleName());
    }

    private <T, ID> boolean isRepositoryForEntity(JpaRepository<T, ID> repository, Class<T> entityType) {
        // Check if the given repository is for the specified entity type
        Type[] genericInterfaces = repository.getClass().getGenericInterfaces();
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                if (paramType.getRawType().equals(JpaRepository.class) &&
                        paramType.getActualTypeArguments()[0].equals(entityType)) {
                    return true;
                }
            }
        }
        return false;
    }
}

