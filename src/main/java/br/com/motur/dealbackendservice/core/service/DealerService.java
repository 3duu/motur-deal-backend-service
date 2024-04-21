package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.DealerRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderRepository;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.DealerDto;
import br.com.motur.dealbackendservice.core.model.AddressEntity;
import br.com.motur.dealbackendservice.core.model.CityEntity;
import br.com.motur.dealbackendservice.core.model.DealerEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealerService /*extends IntegrationService*/ implements DealerServiceInterface {

    final DealerRepository dealerRepository;

    final AddressService addressService;

    final ProviderRepository providerRepository;

    final ObjectMapper objectMapper;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    public DealerService(ObjectMapper objectMapper, DealerRepository dealerRepository, AddressService addressService, ProviderRepository providerRepository) {
        this.dealerRepository = dealerRepository;
        this.addressService = addressService;
        this.providerRepository = providerRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30, propagation = Propagation.REQUIRES_NEW)
    public Integer createDealer(final DealerDto dealerDto) {

        logger.info("Creando dealer: {}", dealerDto);
        if (dealerDto.getId() != null && dealerDto.getId() == 0){
            dealerDto.setId(null);
        }

        logger.info("Creando dealer: {}", dealerDto);
        if (dealerDto.getAddress() != null && dealerDto.getAddress().getId() != null && dealerDto.getAddress().getId() == 0){
            dealerDto.getAddress().setId(null);
        }

        final DealerEntity dealerEntity = dealerRepository.save(DealerEntity.builder()
                .name(dealerDto.getName())
                .cnpj(dealerDto.getCnpj())
                .address(addressService.saveAddress(AddressEntity.builder().id(dealerDto.getAddress().getId()).city(CityEntity.builder().id(dealerDto.getAddress().getCityId()).build())
                        .zipCode(dealerDto.getAddress().getZipCode())
                        .street(dealerDto.getAddress().getStreet())
                        .number(dealerDto.getAddress().getNumber())
                        .complement(dealerDto.getAddress().getComplement())
                        .latitude(dealerDto.getAddress().getLatitude())
                        .longitude(dealerDto.getAddress().getLongitude())
                        .type(dealerDto.getAddress().getType())
                        .build()))
                .phoneNumber(dealerDto.getPhone())
                .email(dealerDto.getEmail())
                .status(dealerDto.getStatus())
                .providers(dealerDto.getProvidersIds() != null ? providerRepository.findAllById(dealerDto.getProvidersIds()) : null)
                .build());

        return dealerEntity.getId();
    }
}
