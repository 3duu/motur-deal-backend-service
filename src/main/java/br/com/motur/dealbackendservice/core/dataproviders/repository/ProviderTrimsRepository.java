package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderModels;
import br.com.motur.dealbackendservice.core.model.ProviderTrims;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderTrimsRepository extends JpaRepository<ProviderTrims, Integer> {
    List<ProviderTrims> findAllByParentProviderCatalog(ProviderModels model);
}
