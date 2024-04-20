package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.DealerRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.FieldMappingRepository;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.DealerDto;
import br.com.motur.dealbackendservice.core.model.DealerEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class DealerService extends IntegrationService implements DealerServiceInterface {

    final DealerRepository dealerRepository;

    @Autowired
    public DealerService(AuthConfigRepository authConfigRepository, FieldMappingRepository fieldMappingRepository, RestTemplate restTemplate, ObjectMapper objectMapper, DealerRepository dealerRepository) {
        super(authConfigRepository, fieldMappingRepository, restTemplate, objectMapper);
        this.dealerRepository = dealerRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30, propagation = Propagation.REQUIRES_NEW)
    public Integer createDealer(final DealerDto dealerDto) {

        final DealerEntity dealerEntity = dealerRepository.save(DealerEntity.builder()
                .name(dealerDto.getName())
                .cnpj(dealerDto.getCnpj())
                .address(dealerDto.getAddress())
                //.phone(dealerDto.getPhone())
                .email(dealerDto.getEmail())
                .status(dealerDto.getStatus())
                .build());

        return dealerEntity.getId();
    }
}
