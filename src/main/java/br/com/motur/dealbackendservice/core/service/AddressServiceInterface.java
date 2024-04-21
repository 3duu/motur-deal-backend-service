package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.model.AddressEntity;

public interface AddressServiceInterface {
    void deleteAddress(Long id);

    void deleteAllAddresses();

    void deleteAllAddresses(Iterable<Long> ids);

    AddressEntity saveAddress(AddressEntity address);

    AddressEntity getAddressById(Long id);
}
