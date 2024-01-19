package br.com.motur.dealbackendservice.core.dataproviders.repository;

import br.com.motur.dealbackendservice.core.model.TrimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrimRepository extends JpaRepository<TrimEntity, Integer> {
    // Aqui você pode adicionar métodos de consulta personalizados, se necessário
}
