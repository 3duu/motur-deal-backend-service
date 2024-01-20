package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.FieldMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldMappingRepository extends JpaRepository<FieldMappingEntity, Integer> {
    List<FieldMappingEntity> findByProviderId(Integer providerId);
    // Aqui você pode adicionar métodos de consulta personalizados, se necessário
}
