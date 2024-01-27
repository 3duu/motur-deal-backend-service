package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Essa classe é responsável por implementar os métodos de serviço de provedor
 */
@Service
public class ProviderServiceImpl {
    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderServiceImpl(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Transactional(readOnly = true)
    public List<ProviderEntity> findAll() {
        return providerRepository.findAll();
    }

    @Transactional
    public ProviderEntity save(ProviderEntity provider) {
        return providerRepository.save(provider);
    }

    @Transactional(readOnly = true)
    public Optional<ProviderEntity> findById(Integer id) {
        return providerRepository.findById(id);
    }

    @Transactional
    public ProviderEntity update(final Integer id, final ProviderEntity providerDetails) {
        ProviderEntity provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found with id " + id));
        provider.setName(providerDetails.getName());
        provider.setApiType(providerDetails.getApiType());
        provider.setUrl(providerDetails.getUrl());
        return providerRepository.save(provider);
    }

    @Transactional
    public void delete(Integer id) {
        providerRepository.deleteById(id);
    }
}
