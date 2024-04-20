package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.entrypoints.v1.request.DealerDto;

public interface DealerServiceInterface {
    Integer createDealer(DealerDto dealerDto);
}
