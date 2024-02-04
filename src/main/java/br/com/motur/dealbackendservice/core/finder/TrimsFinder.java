package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.TrimEntity;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Essa classe é responsável por encontrar entidades de catálogo local
 * @param <T>
 */
public class TrimsFinder extends CatalogFinder<TrimEntity> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public boolean find(final TrimEntity trim, final String nameInProvider) {

        final String normalizedModelName = normalizeName(trim.getName());
        final String normalizedProviderName = normalizeName(nameInProvider);

        final boolean match = trim.getName().trim().toLowerCase().equals(nameInProvider.trim().toLowerCase()) || normalizedModelName.equals(normalizedProviderName)
                || ArrayUtils.contains(trim.getSynonymsArray(), normalizeName(nameInProvider));

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int distance = levenshteinDistance.apply(normalizedModelName, normalizedProviderName);
        int threshold = 2;

        boolean propsMatch = false;

        if (!match || distance <= threshold){

            double similarity = normalizedModelName.length() >= normalizedProviderName.length() ? calculateSimilarity(normalizedModelName, normalizedProviderName) : calculateSimilarity(normalizedProviderName, normalizedModelName);
            //logger.info("Similarity: " + similarity + " - " + normalizedModelName + " - " + normalizedProviderName);
            propsMatch = similarity >= 65 /*|| extractEngineSize(normalizedModelName).equals(extractEngineSize(normalizedProviderName))
                    || extractValveCount(normalizedModelName).equals(extractValveCount(normalizedProviderName))
                    ||  extractModelName(trim).equals(extractModelName(normalizedProviderName))*/;
        }

        return match || propsMatch;
    }

    /**
     * Extracts the engine size from the trim name
     * @param trimName
     * @return
     */
    public String extractEngineSize(String trimName) {
        String regex = "(\\d+(\\.\\d+)?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(trimName);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Extracts the number of valves from the trim name
     * @param trimName
     * @return
     */
    public String extractValveCount(String trimName) {
        String regex = "(\\d+v)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(trimName);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Extracts the model name from the trim name
     * @param trim
     * @return
     */
    public String extractModelName(final TrimEntity trim) {

        return normalizeName(trim.getModel().getName());
    }

    /**
     * Extracts the model name from the trim name
     * @param trimName
     * @return
     */
    public String extractModelName(final String trimName) {

        return trimName.split(" ")[0];
    }

    /**
     * Extracts the model name from the trim name
     * @param trimName
     * @return
     */
    boolean isAutomatic(String trimName, TrimEntity trim) {
        return (trimName.toLowerCase().contains(" automatico ") || trimName.toLowerCase().contains(" aut ") || trimName.toLowerCase().contains(" aut.")) && trim.getTransmissionType().equals(TransmissionType.AUTOMATIC);
    }


    /**
     * Find the entity in the catalog
     * @param entities the entities to be found
     * @param term the term to be found
     * @return the entity found
     */
    @Override
    public TrimEntity find(final List<TrimEntity> entities, final String term) {

        final Map<TrimEntity, Double> odds = new HashMap<>();
        final String normalizedProviderName = normalizeName(term);

        if (entities.size() == 1){
            return entities.get(0);
        }

        for (final TrimEntity entity : entities) {
            //VEICULO COM /
            final String normalizedModelName = normalizeName(entity.getName());

            int distance = levenshteinDistance.apply(normalizedModelName, normalizedProviderName);
            if (distance <= 2 && ArrayUtils.contains(entity.getSynonymsArray(), normalizedProviderName)){
                double similarity = normalizedModelName.length() >= normalizedProviderName.length() ? calculateSimilarity(normalizedModelName, normalizedProviderName) : calculateSimilarity(normalizedProviderName, normalizedModelName);
                odds.put(entity, similarity);
            }

        }

        // If no match is found, try to find a match with the engine size, valve count and model name
        if (odds.isEmpty()) {

            for (final TrimEntity entity : entities) {
                //VEICULO COM /
                if (find(entity, term)) {
                    return entity;
                }
            }
        }

        var maxEntry = odds.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue()).orElse(null);

        return maxEntry != null ? maxEntry.getKey() : entities.stream().filter(e -> find(e, normalizedProviderName)).findFirst().orElse(null);
    }
}
