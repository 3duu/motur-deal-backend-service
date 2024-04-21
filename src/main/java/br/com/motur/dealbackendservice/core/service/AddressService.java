package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.AddressRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.CityRepository;
import br.com.motur.dealbackendservice.core.model.AddressEntity;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService implements AddressServiceInterface {

    private final AddressRepository addressRepository;

    private final CityRepository cityRepository;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    private final ModelMapper modelMapper;

    @Autowired
    public AddressService(AddressRepository addressRepository, CityRepository cityRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.cityRepository = cityRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(rollbackFor = Exception.class, timeout = 60, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class, timeout = 100, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteAllAddresses() {
        addressRepository.deleteAll();
    }

    @Transactional(rollbackFor = Exception.class, timeout = 60, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteAllAddresses(Iterable<Long> ids) {
        ids.forEach(addressRepository::deleteById);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 15, propagation = Propagation.REQUIRES_NEW)
    public AddressEntity saveAddress(final AddressEntity address) {

        final AddressEntity currenAddress = addressRepository.findById(address.getId()).orElse(null);

        if (currenAddress != null) {
            modelMapper.map(address, currenAddress);
            currenAddress.setId(address.getId());
            currenAddress.setCity(cityRepository.findById(address.getCity().getId()).orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada.")));
            return addressRepository.save(currenAddress);
        }

        if (address.getCity() != null && address.getCity().getId() != null) {
            address.setCity(cityRepository.findById(address.getCity().getId()).orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada.")));
        }

        return addressRepository.save(address);
    }

    @Transactional(rollbackFor = Exception.class, timeout = 30, propagation = Propagation.REQUIRES_NEW)
    @Override
    public AddressEntity getAddressById(final Long id) {
        return addressRepository.findById(id).orElse(null);
    }
}
