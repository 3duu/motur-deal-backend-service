package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.BrandRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ProviderBrandsRepository;
import br.com.motur.dealbackendservice.core.model.BrandEntity;
import br.com.motur.dealbackendservice.core.model.ProviderBrandsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService implements IBrandService {

    private final BrandRepository brandRepository;
    private final ProviderBrandsRepository providerBrandsRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository, ProviderBrandsRepository providerBrandsRepository) {
        this.brandRepository = brandRepository;
        this.providerBrandsRepository = providerBrandsRepository;
    }

    @Override
    public List<BrandEntity> findAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public BrandEntity findBrandByName(String name) {
        return brandRepository.getByName(name).orElse(null);
    }

    @Override
    public BrandEntity findById(Integer brandId) {
        return brandRepository.findById(brandId).orElse(null);
    }

    @Override
    public ProviderBrandsEntity findByIdProvider(Integer brandId, Integer providerId) {
        return providerBrandsRepository.findByBaseCatalogandProvider(brandId, providerId).orElse(null);
    }

}
