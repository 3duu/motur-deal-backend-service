package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderTrimsMinimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderMinimalTrimsRepository extends JpaRepository<ProviderTrimsMinimalEntity, Long> {

    @Query(value = "SELECT pt FROM ProviderTrimsMinimalEntity pt inner join fetch pt.baseCatalog WHERE pt.id = ?1")
    Optional<ProviderTrimsMinimalEntity> findFullById(Long providerTrimId);
}
