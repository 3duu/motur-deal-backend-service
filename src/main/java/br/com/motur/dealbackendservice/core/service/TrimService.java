package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderTrimsRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.TrimRepository;
import br.com.motur.dealbackendservice.core.model.ProviderTrimsEntity;
import br.com.motur.dealbackendservice.core.model.TrimEntity;
import br.com.motur.dealbackendservice.core.model.common.CacheNames;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TrimService {

    private final TrimRepository trimRepository;
    private final ProviderTrimsRepository providerTrimsRepository;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    public TrimService(TrimRepository trimRepository, ProviderTrimsRepository providerTrimsRepository) {
        this.trimRepository = trimRepository;
        this.providerTrimsRepository = providerTrimsRepository;
    }

    @Cacheable(value=CacheNames.BASE_CATALOG_TRIMS, key = "#trimId", unless = "#result == null")
    public TrimEntity findById(Integer trimId) {

        logger.info("Buscando versão por id: {}", trimId);
        return trimRepository.findById(trimId)
                .orElseThrow(() -> new RuntimeException("Versão não encontrada para o id: " + trimId));
    }

    @Cacheable(value=CacheNames.BASE_CATALOG_TRIMS, key = "#trimId", unless = "#result == null")
    public TrimEntity findFullById(Integer trimId) {
        logger.info("Buscando versão completa por id: {}", trimId);
        return trimRepository.findFullById(trimId)
                .orElseThrow(() -> new RuntimeException("Versão não encontrada para o id: " + trimId));
    }

    @Cacheable(value=CacheNames.PROVIDER_CATALOG_TRIMS, key = "#providerTrimId", unless = "#result == null")
    public ProviderTrimsEntity findProviderById(final Long providerTrimId) {
        logger.info("Buscando versão do integrador por id: {}", providerTrimId);
        return providerTrimsRepository.findById(providerTrimId)
                .orElseThrow(() -> new RuntimeException("Versão do integrador não encontrada para o id: " + providerTrimId));
    }


    // Métodos de serviço para interagir com Trim
}
