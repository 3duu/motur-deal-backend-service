package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.BrandEntity;
import org.apache.commons.lang3.ArrayUtils;


public class BrandsFinder implements CatalogFinder<BrandEntity> {

    @Override
    public boolean find(final BrandEntity entity, String term) {
        term = term.toLowerCase().trim();
        return entity.getName().equals(term) || normalizeName(entity.getName()).equals(normalizeName(term))
                || ArrayUtils.contains(entity.getSynonymsArray(), term.toLowerCase()) || ArrayUtils.contains(entity.getSynonymsArray(), normalizeName(term));
    }

    private String normalizeName(final String name) {
        if (name == null) {
            return "";
        }
        // Remova espaços, converta para minúsculas e faça outras normalizações conforme necessário

        return name.trim()
                .replaceAll("\\s+", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase();
    }
}
