package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderBrands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderBrandsRepository extends JpaRepository<ProviderBrands, Integer> {
}
