package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.ProviderBrandsEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderBrandsRepository extends JpaRepository<ProviderBrandsEntity, Integer> {
    List<ProviderBrandsEntity> findAllByProvider(ProviderEntity provider);

    @Query("SELECT pb FROM ProviderBrandsEntity pb inner join fetch pb.provider p inner join fetch pb.baseCatalog bc WHERE p.id = ?1")
    List<ProviderBrandsEntity> findAllByProviderId(Integer id);

    @Query("SELECT pb FROM ProviderBrandsEntity pb inner join fetch pb.provider p inner join fetch pb.baseCatalog bc WHERE bc.id = ?1")
    Optional<ProviderBrandsEntity> findByBaseCatalogandProvider(Integer brandId, Integer providerId);
}
