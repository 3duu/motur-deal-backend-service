package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderBrandsRepository extends JpaRepository<ProviderBrands, Integer> {
    List<ProviderBrands> findAllByProvider(ProviderEntity provider);
}