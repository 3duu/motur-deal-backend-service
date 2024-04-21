package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AuthConfigRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.DealerRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.FieldMappingRepository;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.DealerDto;
import br.com.motur.dealbackendservice.core.model.DealerEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class DealerService extends IntegrationService implements DealerServiceInterface {

    final DealerRepository dealerRepository;

    final AddressService addressService;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    public DealerService(AuthConfigRepository authConfigRepository, FieldMappingRepository fieldMappingRepository, RestTemplate restTemplate, ObjectMapper objectMapper, DealerRepository dealerRepository, AddressService addressService) {
        super(authConfigRepository, fieldMappingRepository, restTemplate, objectMapper);
        this.dealerRepository = dealerRepository;
        this.addressService = addressService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30, propagation = Propagation.REQUIRES_NEW)
    public Integer createDealer(final DealerDto dealerDto) {

        logger.info("Creando dealer: {}", dealerDto);
        if (dealerDto.getId() != null && dealerDto.getId() == 0){
            dealerDto.setId(null);
        }

        if (dealerDto.getAddress() != null && dealerDto.getAddress().getId() != null && dealerDto.getAddress().getId() == 0){
            dealerDto.getAddress().setId(null);
        }

        final DealerEntity dealerEntity = dealerRepository.save(DealerEntity.builder()
                .name(dealerDto.getName())
                .cnpj(dealerDto.getCnpj())
                .address(addressService.saveAddress(dealerDto.getAddress()))
                .phoneNumber(dealerDto.getPhone())
                .email(dealerDto.getEmail())
                .status(dealerDto.getStatus())
                .build());

        return dealerEntity.getId();
    }
}
