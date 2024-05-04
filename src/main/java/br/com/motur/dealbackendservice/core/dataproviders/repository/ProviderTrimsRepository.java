package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderTrimsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderTrimsRepository extends JpaRepository<ProviderTrimsEntity, Long> {

    @Query(value = "SELECT pt FROM ProviderTrimsEntity pt inner join fetch pt.provider p inner join fetch pt.parentProviderCatalog ppc inner join fetch pt.baseCatalog WHERE p.id = ?1")
    List<ProviderTrimsEntity> findAllByParentProviderCatalog(Long modelId);

    @Query(value = "SELECT pt FROM ProviderTrimsEntity pt inner join pt.provider p inner join fetch pt.parentProviderCatalog ppc inner join pt.baseCatalog inner join fetch ppc.parentProviderCatalog WHERE p.id = ?1 and pt.baseCatalog.id = ?2")
    Optional<ProviderTrimsEntity> findByProviderIdAndBaseCatalogTrimId(Integer providerId, Integer trimId);

    @Query(value = "SELECT pt FROM ProviderTrimsEntity pt inner join fetch pt.provider p inner join fetch pt.parentProviderCatalog ppc inner join fetch pt.baseCatalog inner join fetch ppc.parentProviderCatalog prc WHERE pt.id = ?1")
    Optional<ProviderTrimsEntity> findFullById(Long providerTrimId);
}
