package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderModelsEntity;
import br.com.motur.dealbackendservice.core.model.ProviderTrimsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderTrimsRepository extends JpaRepository<ProviderTrimsEntity, Integer> {

    @Query(value = "SELECT pt FROM ProviderTrimsEntity pt inner join fetch pt.provider p inner join fetch pt.parentProviderCatalog ppc inner join fetch pt.baseCatalog WHERE p.id = ?1")
    List<ProviderTrimsEntity> findAllByParentProviderCatalog(Integer modelId);
}
