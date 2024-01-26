package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import br.com.motur.dealbackendservice.core.model.ProviderModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderModelsRepository extends JpaRepository<ProviderModels, Integer> {
    List<ProviderModels> findAllByParentProviderCatalog(ProviderBrands parentProviderCatalog);

    @Query(value = "SELECT pm FROM ProviderModels pm inner join fetch pm.provider p inner join fetch pm.parentProviderCatalog ppc inner join fetch pm.baseModel WHERE p.id = ?1")
    List<ProviderModels> findAllByProviderId(Integer id);
}
