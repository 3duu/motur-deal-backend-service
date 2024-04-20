package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderBrandsEntity;
import br.com.motur.dealbackendservice.core.model.ProviderModelsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderModelsRepository extends JpaRepository<ProviderModelsEntity, Integer> {
    List<ProviderModelsEntity> findAllByParentProviderCatalog(ProviderBrandsEntity parentProviderCatalog);

    @Query(value = "SELECT pm FROM ProviderModelsEntity pm inner join fetch pm.provider p inner join fetch pm.parentProviderCatalog ppc inner join fetch pm.baseModel WHERE p.id = ?1")
    List<ProviderModelsEntity> findAllByProviderId(Integer id);
}
