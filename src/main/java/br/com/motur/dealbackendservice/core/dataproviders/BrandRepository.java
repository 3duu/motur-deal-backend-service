package br.com.motur.dealbackendservice.core.dataproviders;

import br.com.motur.dealbackendservice.core.model.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Integer> {

}

