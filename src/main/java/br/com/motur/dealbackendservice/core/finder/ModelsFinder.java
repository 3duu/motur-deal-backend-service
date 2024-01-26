package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.ModelEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;


public class ModelsFinder extends CatalogFinder<ModelEntity> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean find(final ModelEntity model, String nameInProvider) {
        String normalizedModelName = normalizeName(model, model.getName());
        String normalizedProviderName = normalizeName(model, nameInProvider);

        boolean match = model.getName().trim().toLowerCase().equals(nameInProvider.trim().toLowerCase()) || normalizedModelName.equals(normalizedProviderName)
                || ArrayUtils.contains(model.getSynonymsArray(), nameInProvider.toLowerCase()) || ArrayUtils.contains(model.getSynonymsArray(), normalizeName(null, nameInProvider));

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int distance = levenshteinDistance.apply(normalizedModelName, normalizedProviderName);
        int threshold = 1; // Defina um limiar adequado

        //return distance <= threshold;

        return match || distance <= threshold;
    }

    private String normalizeName(final ModelEntity model, final String name) {
        if (name == null) {
            return "";
        }

        return Normalizer.normalize(name.trim()
                .replaceAll("\\s+", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .replace(model != null ? normalizeName(null, model.getBrand().getName()) : "", "")
                .toLowerCase(), Normalizer.Form.NFD);
    }
}
