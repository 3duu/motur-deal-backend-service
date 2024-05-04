package br.com.motur.dealbackendservice.core.service.vo;

import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.CacheableEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;


@Getter
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderTrimVo implements CacheableEntity {

    private Long id;
    private String name;
    private ProviderEntity provider;
    private TrimEntity baseCatalog;
    private String externalId;

    private ProviderModelsEntity providerModelsEntity;
    private ProviderBrandsEntity providerBrandsEntity;

    @Override
    public String getCacheKey() {
        return getProvider().getId() + ":" + externalId + ":" + (baseCatalog != null ? baseCatalog.getId() : StringUtils.EMPTY) + ":" + (providerModelsEntity != null ? providerModelsEntity.getId() : StringUtils.EMPTY) + ":" + (providerBrandsEntity != null ? providerBrandsEntity.getId() : StringUtils.EMPTY);
    }
}
