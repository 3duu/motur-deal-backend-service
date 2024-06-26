package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.EndpointConfigEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndpointConfigRepository extends JpaRepository<EndpointConfigEntity, Integer> {

    @Query("SELECT ec FROM EndpointConfigEntity ec inner join fetch ec.provider p where ec.category = ?1 and p = ?2 order by ec.executionOrder asc")
    List<EndpointConfigEntity> findByCategoryAndProvider(EndpointCategory endpointCategory, ProviderEntity provider);

}
