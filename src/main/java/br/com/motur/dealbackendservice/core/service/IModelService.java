package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.model.ModelEntity;

public interface IModelService {

    ModelEntity findById(Integer modelId);
}
