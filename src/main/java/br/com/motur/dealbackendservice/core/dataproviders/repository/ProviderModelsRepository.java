package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import br.com.motur.dealbackendservice.core.model.ProviderModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderModelsRepository extends JpaRepository<ProviderModels, Integer> {
    List<ProviderModels> findAllByParentProviderCatalog(ProviderBrands parentProviderCatalog);
}
