package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.AuthConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthConfigRepository extends JpaRepository<AuthConfigEntity, Integer> {
    AuthConfigEntity findByProviderId(Integer providerId);

}
