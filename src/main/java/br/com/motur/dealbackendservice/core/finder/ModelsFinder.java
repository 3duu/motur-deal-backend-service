package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.ModelEntity;
import org.apache.commons.lang3.ArrayUtils;


public class ModelsFinder implements CatalogFinder<ModelEntity> {

    @Override
    public boolean find(final ModelEntity model, String nameInProvider) {
        String normalizedModelName = normalizeName(model, model.getName());
        String normalizedProviderName = normalizeName(model, nameInProvider);

        boolean match = model.getName().trim().toLowerCase().equals(nameInProvider.trim().toLowerCase()) || normalizedModelName.equals(normalizedProviderName)
                || ArrayUtils.contains(model.getSynonymsArray(), nameInProvider.toLowerCase()) || ArrayUtils.contains(model.getSynonymsArray(), normalizeName(null, nameInProvider));

        /*if (!match){
            System.out.println("Modelo não encontrado: " + model.getName() + " - " + nameInProvider);
        }*/

        return match;
    }

    private String normalizeName(final ModelEntity model, final String name) {
        if (name == null) {
            return "";
        }

        return name.trim()
                .replaceAll("\\s+", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .replace(model != null ? normalizeName(null, model.getBrand().getName()) : "", "")
                .toLowerCase();
    }
}
