package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.core.service.vo.AdVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class VoTitleHelper implements ValueHelper<AdVo,String> {

    @Override
    public String getValue(AdVo value) {
        return value.getTitle();
    }

    @Override
    public boolean isNull(AdVo object) {
        return StringUtils.isEmpty(object.getTitle());
    }

    @Override
    public String getDefaultValue(final AdVo adEntity, final Object reference) {

        if (isNull(adEntity)){

            return adEntity.getBaseModel().getBrand().getName() + " " + adEntity.getBaseModel().getName() + " " + adEntity.getBaseTrim().getName().replace(adEntity.getBaseModel().getName(), StringUtils.EMPTY) + " " + adEntity.getModelYear() + " " + adEntity.getTransmissionType().getDisplayName();

        }
        else {
            return adEntity.getTitle();
        }
    }
    
}
