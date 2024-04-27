package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Integer> {

    Optional<BrandEntity> getByName(String name);
    ;
}

