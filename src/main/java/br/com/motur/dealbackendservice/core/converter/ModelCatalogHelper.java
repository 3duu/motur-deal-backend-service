package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.model.AdEntity;
import br.com.motur.dealbackendservice.core.model.ModelEntity;
import br.com.motur.dealbackendservice.core.service.IModelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelCatalogHelper implements ValueHelper<AdEntity, String> {

    private final IModelService modelService;

    @Autowired
    public ModelCatalogHelper(IModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public String getValue(AdEntity value) {
        return value.getModelId().toString();
    }

    @Override
    public boolean isNull(AdEntity adEntity) {
        return StringUtils.isEmpty(adEntity.getModelId() != null ? adEntity.getModelId().toString() : StringUtils.EMPTY);
    }

    @Override
    public String getDefaultValue(final AdEntity adEntity, final FieldMappingInfo fieldMappingInfo) {

        if (!isNull(adEntity)){

            if (adEntity.getTrimId() == null){
                return null;
            }

            final ModelEntity modelEntity = modelService.findById(adEntity.getModelId());
            if (modelEntity == null){
                return adEntity.getTrimId().toString();
            }

            return modelEntity.getName();

        }
        else {
            return adEntity.getTrimId().toString();
        }
    }


}
