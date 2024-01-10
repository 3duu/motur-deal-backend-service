package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.BrandRepository;
import br.com.motur.dealbackendservice.core.model.BrandEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService implements IBrandService {

    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<BrandEntity> findAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public BrandEntity findBrandByName(String name) {
        return brandRepository.getByName(name).orElse(null);
    }

}
