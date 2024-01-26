package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.CatalogEntity;
import br.com.motur.dealbackendservice.core.model.TrimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrimRepository extends JpaRepository<TrimEntity, Integer> {

    @Query(value = "SELECT t FROM TrimEntity t inner join fetch t.model m WHERE m.id = ?1")
    List<TrimEntity> findAllByModelId(Integer modelId);
    // Aqui você pode adicionar métodos de consulta personalizados, se necessário
}
