package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.model.AdEntity;
import br.com.motur.dealbackendservice.core.model.ModelEntity;
import br.com.motur.dealbackendservice.core.model.TrimEntity;
import br.com.motur.dealbackendservice.core.service.ModelService;
import br.com.motur.dealbackendservice.core.service.TrimService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrimCatalogHelper implements ValueHelper<AdEntity, String> {

    private final TrimService trimService;

    @Autowired
    public TrimCatalogHelper(TrimService trimService) {
        this.trimService = trimService;
    }

    @Override
    public String getValue(AdEntity value) {
        return value.getTrimId().toString();
    }

    @Override
    public boolean isNull(AdEntity adEntity) {
        return StringUtils.isEmpty(adEntity.getTrimId() != null ? adEntity.getTrimId().toString() : StringUtils.EMPTY);
    }

    @Override
    public String getDefaultValue(final AdEntity adEntity, final FieldMappingInfo fieldMappingInfo) {

        if (!isNull(adEntity)){

            if (adEntity.getTrimId() == null){
                return null;
            }

            final TrimEntity trimEntity = trimService.findById(adEntity.getTrimId());
            if (trimEntity == null){
                return adEntity.getTrimId().toString();
            }

            return trimEntity.getName();

        }
        else {
            return adEntity.getTrimId().toString();
        }
    }


}
