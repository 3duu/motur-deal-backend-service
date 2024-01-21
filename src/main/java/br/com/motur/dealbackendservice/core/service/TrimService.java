package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.TrimRepository;
import br.com.motur.dealbackendservice.core.model.TrimEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrimService {

    private final TrimRepository trimRepository;

    @Autowired
    public TrimService(TrimRepository trimRepository) {
        this.trimRepository = trimRepository;
    }

    public TrimEntity findById(Integer trimId) {
        return trimRepository.findById(trimId)
                .orElseThrow(() -> new RuntimeException("Trim not found with id " + trimId));
    }

    // Métodos de serviço para interagir com Trim
}
