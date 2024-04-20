package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.entrypoints.v1.request.AdDto;

import java.util.List;

public interface AdPublicationServiceInterface {
    void publishAd(final AdDto adDto);

    AdDto getAdDto(final Long id);

    List<AdDto> getAdsPageable(Integer page, Integer size);
}
