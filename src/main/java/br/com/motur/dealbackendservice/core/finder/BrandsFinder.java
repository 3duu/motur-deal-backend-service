package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.BrandEntity;
import org.apache.commons.lang3.ArrayUtils;


public class BrandsFinder implements CatalogFinder<BrandEntity> {

    @Override
    public boolean find(final BrandEntity entity, String term) {
        term = term.toLowerCase().trim();
        return entity.getName().equals(term) || ArrayUtils.contains(entity.getSynonymsArray(), term.toLowerCase());
    }
}
