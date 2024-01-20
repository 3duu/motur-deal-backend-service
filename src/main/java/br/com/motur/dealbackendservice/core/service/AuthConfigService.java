package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.model.AuthConfigEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthConfigService {

    private final AuthConfigRepository authConfigRepository;

    public AuthConfigService(AuthConfigRepository authConfigRepository) {
        this.authConfigRepository = authConfigRepository;
    }

    @Transactional
    public AuthConfigEntity save(AuthConfigEntity authConfig) {
        return authConfigRepository.save(authConfig);
    }

    @Transactional(readOnly = true)
    public List<AuthConfigEntity> findAll() {
        return authConfigRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<AuthConfigEntity> findById(Integer id) {
        return authConfigRepository.findById(id);
    }

    @Transactional
    public AuthConfigEntity update(Integer id, AuthConfigEntity authConfigDetails) {
        AuthConfigEntity authConfig = authConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AuthConfig not found"));
        // Atualize os campos de authConfig com authConfigDetails
        return authConfigRepository.save(authConfig);
    }

    @Transactional
    public void delete(Integer id) {
        authConfigRepository.deleteById(id);
    }
}
