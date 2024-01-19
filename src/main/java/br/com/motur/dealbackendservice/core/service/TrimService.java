package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.TrimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrimService {

    private final TrimRepository trimRepository;

    @Autowired
    public TrimService(TrimRepository trimRepository) {
        this.trimRepository = trimRepository;
    }

    // Métodos de serviço para interagir com Trim
}
