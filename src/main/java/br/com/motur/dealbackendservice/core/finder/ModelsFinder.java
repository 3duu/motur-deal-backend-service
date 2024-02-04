package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.ModelEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Essa classe é responsável por encontrar entidades de catálogo local
 * @param <T> Tipo da entidade de catálogo
 */

public class ModelsFinder extends CatalogFinder<ModelEntity> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean find(final ModelEntity model, String nameInProvider) {
        String normalizedModelName = normalizeName(model.getName());
        String normalizedProviderName = normalizeName(nameInProvider);

        boolean match = model.getName().trim().toLowerCase().equals(nameInProvider.trim().toLowerCase()) || normalizedModelName.equals(normalizedProviderName)
                || ArrayUtils.contains(model.getSynonymsArray(), nameInProvider.toLowerCase()) || ArrayUtils.contains(model.getSynonymsArray(), normalizeName(nameInProvider));

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int distance = levenshteinDistance.apply(normalizedModelName, normalizedProviderName);
        int threshold = 1; // Defina um limiar adequado

        return match || distance <= threshold;
    }

    @Override
    public ModelEntity find(final List<ModelEntity> entities, String term) {

        String normalizedProviderName = normalizeName(term);
        for (ModelEntity model : entities) {

            String normalizedModelName = normalizeName(model.getName());
            boolean match = model.getName().trim().toLowerCase().equals(term.trim().toLowerCase()) || normalizedModelName.equals(normalizedProviderName)
                    || ArrayUtils.contains(model.getSynonymsArray(), term.toLowerCase()) || ArrayUtils.contains(model.getSynonymsArray(), normalizeName(term));

            if (match) {
                return model;
            }
        }

        for (ModelEntity model : entities) {

            String normalizedModelName = normalizeName(model.getName());
            LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
            int distance = levenshteinDistance.apply(normalizedModelName, normalizedProviderName);
            int threshold = 1; // Defina um limiar adequado

            if (distance <= threshold) {
                return model;
            }
        }

        return null;
    }
}
