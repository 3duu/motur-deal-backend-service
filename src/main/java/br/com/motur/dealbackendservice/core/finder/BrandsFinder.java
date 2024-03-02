package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.BrandEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Essa classe é responsável por encontrar entidades de catálogo local
 * @param <T> Tipo da entidade de catálogo
 */
public class BrandsFinder extends CatalogFinder<BrandEntity> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public boolean find(final BrandEntity entity, String term) {
        term = term.toLowerCase().trim();

        int distance = levenshteinDistance.apply(normalizeName(entity.getName()), normalizeName(term));
        int threshold = 1; // Defina um limiar adequado

        return entity.getName().equals(term) || normalizeName(entity.getName()).equals(normalizeName(term))
                || ArrayUtils.contains(entity.getSynonymsArray(), term.toLowerCase()) || ArrayUtils.contains(entity.getSynonymsArray(), normalizeName(term)) || distance <= threshold;
    }

    @Override
    public BrandEntity find(final List<BrandEntity> entities, String term) {

        for (BrandEntity entity : entities) {

            if (find(entity, term)) {
                return entity;
            }
        }

       return null;
    }
}
