package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.TrimRepository;
import br.com.motur.dealbackendservice.core.model.TrimEntity;
import br.com.motur.dealbackendservice.core.model.common.CacheNames;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TrimService {

    private final TrimRepository trimRepository;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    public TrimService(TrimRepository trimRepository) {
        this.trimRepository = trimRepository;
    }

    @Cacheable(value=CacheNames.BASE_CATALOG_TRIMS, key = "#trimId")
    public TrimEntity findById(Integer trimId) {

        logger.info("Buscando versão por id: {}", trimId);
        return trimRepository.findById(trimId)
                .orElseThrow(() -> new RuntimeException("Versão não encontrada para o id: " + trimId));
    }



    // Métodos de serviço para interagir com Trim
}
