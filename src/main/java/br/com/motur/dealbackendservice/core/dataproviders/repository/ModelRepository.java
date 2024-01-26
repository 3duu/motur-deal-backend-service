package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.BrandEntity;
import br.com.motur.dealbackendservice.core.model.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<ModelEntity, Integer> {
    List<ModelEntity> findAllByBrandId(Integer brandId);


    @Query("SELECT m FROM ModelEntity m inner join fetch m.brand b WHERE b.id = ?1")
    List<ModelEntity> findAllByBrand(Integer id);
    // Aqui você pode adicionar métodos de consulta personalizados, se necessário
}
