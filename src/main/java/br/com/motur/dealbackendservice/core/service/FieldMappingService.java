package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.FieldMappingRepository;
import br.com.motur.dealbackendservice.core.model.FieldMappingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FieldMappingService {

    private final FieldMappingRepository fieldMappingRepository;

    @Autowired
    public FieldMappingService(FieldMappingRepository fieldMappingRepository) {
        this.fieldMappingRepository = fieldMappingRepository;
    }

    @Transactional
    public FieldMappingEntity save(FieldMappingEntity fieldMapping) {
        return fieldMappingRepository.save(fieldMapping);
    }

    @Transactional(readOnly = true)
    public List<FieldMappingEntity> findAll() {
        return fieldMappingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<FieldMappingEntity> findById(Integer id) {
        return fieldMappingRepository.findById(id);
    }

    @Transactional
    public FieldMappingEntity update(Integer id, FieldMappingEntity fieldMappingDetails) {
        FieldMappingEntity fieldMapping = fieldMappingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FieldMapping not found with id " + id));
        // Aqui você atualiza os campos de fieldMapping com fieldMappingDetails
        return fieldMappingRepository.save(fieldMapping);
    }

    @Transactional
    public void delete(Integer id) {
        fieldMappingRepository.deleteById(id);
    }
}
