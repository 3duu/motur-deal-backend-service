package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.core.model.AdEntity;
import br.com.motur.dealbackendservice.core.model.TrimEntity;
import br.com.motur.dealbackendservice.core.service.TrimService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TitleHelper implements ValueHelper<AdEntity,String> {

    private final TrimService trimService;

    public TitleHelper(TrimService trimService) {
        this.trimService = trimService;
    }


    @Override
    public String getValue(AdEntity value) {
        return value.getTitle();
    }

    @Override
    public boolean isNull(AdEntity object) {
        return StringUtils.isEmpty(object.getTitle());
    }

    @Override
    public String getDefaultValue(final AdEntity adEntity, final Object reference) {

        if (isNull(adEntity)){

            if (adEntity.getTrimId() == null){
                return adEntity.getTitle();
            }

            final TrimEntity trimEntity = trimService.findFullById(adEntity.getTrimId());
            if (trimEntity == null){
                return adEntity.getTitle();
            }

            return trimEntity.getModel().getBrand().getName() + " " + trimEntity.getModel().getName() + " " + trimEntity.getName().replace(trimEntity.getModel().getName(), StringUtils.EMPTY) + " " + adEntity.getModelYear() + " " + adEntity.getTransmissionType().getDisplayName();

        }
        else {
            return adEntity.getTitle();
        }
    }


}
