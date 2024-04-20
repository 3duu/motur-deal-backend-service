package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.entrypoints.v1.request.AdDto;
import br.com.motur.dealbackendservice.core.service.vo.PostResultsVo;

import java.util.List;

public interface AdPublicationServiceInterface {
    PostResultsVo publishAd(final AdDto adDto) throws Exception;

    AdDto getAdDto(final Long id);

    List<AdDto> getAdsPageable(Integer page, Integer size);
}
