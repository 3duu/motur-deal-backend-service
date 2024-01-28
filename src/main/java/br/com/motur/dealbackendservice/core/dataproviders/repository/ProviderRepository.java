package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, Integer> {
    // Métodos CRUD adicionais e consultas personalizadas podem ser definidos aqui, se necessário

    @Query("SELECT p FROM ProviderEntity p WHERE p.name = ?1 and p.active = true")
    ProviderEntity findByName(String name);

    @Query("SELECT p FROM ProviderEntity p WHERE p.id = ?1 and p.active = true")
    @Override
    Optional<ProviderEntity> findById(Integer integer);

    @Query("SELECT p FROM ProviderEntity p WHERE p.active = true")
    List<ProviderEntity> findActives();

    @Override
    @Query("SELECT p FROM ProviderEntity p")
    List<ProviderEntity> findAll();

    @Query("SELECT p FROM ProviderEntity p WHERE p.active = true and p.autoDownloadCatalog = true")
    List<ProviderEntity> findAllAutoDownloadCatalog();
}
