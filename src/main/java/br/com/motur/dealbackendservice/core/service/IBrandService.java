package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.model.BrandEntity;
import br.com.motur.dealbackendservice.core.model.ProviderBrandsEntity;

import java.util.List;

public interface IBrandService {
    List<BrandEntity> findAllBrands();

    BrandEntity findBrandByName(String name);

    BrandEntity findById(Integer trimId);

    ProviderBrandsEntity findByIdProvider(Integer brandId, Integer providerId);
}
