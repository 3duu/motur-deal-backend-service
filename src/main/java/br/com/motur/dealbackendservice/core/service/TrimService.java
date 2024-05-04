package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderMinimalTrimsRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderTrimsRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.TrimRepository;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.CacheNames;
import br.com.motur.dealbackendservice.core.service.vo.ProviderTrimVo;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TrimService {

    private final TrimRepository trimRepository;
    private final ProviderTrimsRepository providerTrimsRepository;
    private final ProviderMinimalTrimsRepository providerMinimalTrimsRepository;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    public TrimService(TrimRepository trimRepository, ProviderTrimsRepository providerTrimsRepository, ProviderMinimalTrimsRepository providerMinimalTrimsRepository) {
        this.trimRepository = trimRepository;
        this.providerTrimsRepository = providerTrimsRepository;
        this.providerMinimalTrimsRepository = providerMinimalTrimsRepository;
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

    @Cacheable(value=CacheNames.PROVIDER_CATALOG_TRIMS_VO, key = "#providerTrimId", unless = "#result == null")
    public ProviderTrimVo findProviderById(final Long providerTrimId) {
        logger.info("Buscando versão do integrador por id: {}", providerTrimId);
        final ProviderTrimsEntity trim = providerTrimsRepository.findFullById(providerTrimId)
                .orElseThrow(() -> new RuntimeException("Versão do integrador não encontrada para o id: " + providerTrimId));

        trim.getBaseCatalog().setModel(null);
        return ProviderTrimVo.builder().id(trim.getId())
                .name(trim.getName())
                .provider(trim.getProvider())
                .baseCatalog(trim.getBaseCatalog())
                .externalId(trim.getExternalId())
                .providerModelsEntity((ProviderModelsEntity)trim.getParentProviderCatalog())
                .providerBrandsEntity((ProviderBrandsEntity)trim.getParentProviderCatalog().getParentProviderCatalog())
                .build();
    }

    @Cacheable(value=CacheNames.PROVIDER_CATALOG_TRIMS, key = "#providerTrimId", unless = "#result == null")
    public ProviderTrimsEntity findProviderEntityById(final Long providerTrimId, Integer provider) {
        logger.info("Buscando versão do integrador por id: {}", providerTrimId);
        final ProviderTrimsEntity trim = providerTrimsRepository.findFullById(providerTrimId)
                .orElseThrow(() -> new RuntimeException("Versão do integrador não encontrada para o id: " + providerTrimId));

        var newTrim = new ModelMapper().map(trim, ProviderTrimsEntity.class);
        newTrim.setProvider(ProviderEntity.builder().id(provider).build());
        newTrim.getParentProviderCatalog().setProvider(trim.getProvider());
        newTrim.getParentProviderCatalog().getParentProviderCatalog().setProvider(null);
        newTrim.getParentProviderCatalog().setProvider(null);
        newTrim.setBaseCatalog(TrimEntity.builder().id(trim.getBaseCatalog().getId()).build());
        newTrim.getParentProviderCatalog().setBaseCatalog(null);
        newTrim.getParentProviderCatalog().getParentProviderCatalog().setBaseCatalog(null);


        return newTrim;
    }


    // Métodos de serviço para interagir com Trim
}
