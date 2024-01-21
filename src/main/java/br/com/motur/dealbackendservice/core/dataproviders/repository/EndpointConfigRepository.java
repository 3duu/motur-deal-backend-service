package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.EndpointConfig;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndpointConfigRepository extends JpaRepository<EndpointConfig, Integer> {
    List<EndpointConfig> findByCategory(EndpointCategory category);

    List<EndpointConfig> findByCategoryAndProvider(EndpointCategory endpointCategory, ProviderEntity provider);
}
