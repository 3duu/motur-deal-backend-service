package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.core.model.AdEntity;
import br.com.motur.dealbackendservice.core.model.ProviderBrandsEntity;
import br.com.motur.dealbackendservice.core.service.IBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BrandCatalogHelper implements ValueHelper<AdEntity, String> {

    private final IBrandService brandService;

    @Autowired
    public BrandCatalogHelper(IBrandService brandService) {
        this.brandService = brandService;
    }

    @Override
    public String getValue(AdEntity value) {
        return value.getBrandId().toString();
    }

    @Override
    public boolean isNull(AdEntity adEntity) {
        return StringUtils.isEmpty(adEntity.getTrimId() != null ? adEntity.getTrimId().toString() : StringUtils.EMPTY);
    }

    @Override
    public String getDefaultValue(final AdEntity adEntity, final Object reference) {

        if (!isNull(adEntity)){

            if (adEntity.getTrimId() == null){
                return null;
            }

            final ProviderBrandsEntity brandEntity = brandService.findByIdProvider(adEntity.getBrandId(), Integer.valueOf(reference.toString()));
            if (brandEntity == null){
                return adEntity.getTrimId().toString();
            }

            return brandEntity.getExternalId();
        }

        return adEntity.getTrimId().toString();
    }


}
