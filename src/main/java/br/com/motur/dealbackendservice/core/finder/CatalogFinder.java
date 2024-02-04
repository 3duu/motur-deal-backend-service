package br.com.motur.dealbackendservice.core.finder;


import br.com.motur.dealbackendservice.core.model.CatalogEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Essa classe é responsável por encontrar entidades de catálogo local
 * @param <T>
 */
public abstract class CatalogFinder<T extends CatalogEntity> {


    protected LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    /*protected void find(){
        List<RawData> listFrom = rawDataService.findAllBySegmentProviderType(segment, from, type);
        List<RawData> listTo = rawDataService.findAllBySegmentProviderType(segment, to, type);

        List<String> collectFrom = listFrom.stream().map(s -> s.getLabel().toLowerCase()).collect(Collectors.toList());
        List<String> collectTo = listTo.stream().map(s -> s.getLabel().toLowerCase()).collect(Collectors.toList());

        Map<String, String> stringStringMap = stringMatchService.calculateProximity(collectFrom, collectTo, 1);

        Set<String> strings = stringStringMap.keySet();
        for(String s: strings){
            if(stringStringMap.get(s)==null)
                continue;

            Optional<RawData> parentIcarros
                    = listFrom.stream()
                    .filter(icarros -> icarros.getLabel().toLowerCase().equals(s.toLowerCase()))
                    .findFirst();

            Optional<RawData> itemTo
                    = listTo.stream()
                    .filter(item -> item.getLabel().toLowerCase().equals(stringStringMap.get(s).toLowerCase()))
                    .findFirst();

            if (parentIcarros.isPresent() && itemTo.isPresent())
                this.persistAdapter(parentIcarros.get(),itemTo.get());
        }
    }*/

    /**
     * Find the entity in the catalog
     * @param providerCatalogEntity The entity to be found
     * @param text
     * @return true if the entity is found
     */
    public abstract boolean find(T providerCatalogEntity, String text);


    /**
     * Find the entity in the catalog
     * @param entities
     * @param term
     * @return
     */
    public T find(final List<T> entities, final String term) {

        final Map<T, Double> odds = new HashMap<>();
        final String normalizedProviderName = normalizeName(term);
        var str = normalizedProviderName.split(" ");

        if (entities.size() == 1){
            return entities.get(0);
        }

        for (T entity : entities) {
            //VEICULO COM /
            final String normalizedModelName = normalizeName(entity.getName());

            int distance = levenshteinDistance.apply(normalizedModelName, normalizedProviderName);
            if (distance <= 2 && ArrayUtils.contains(entity.getSynonymsArray(), normalizedProviderName)){
                double similarity = normalizedModelName.length() >= normalizedProviderName.length() ? calculateSimilarity(normalizedModelName, normalizedProviderName) : calculateSimilarity(normalizedProviderName, normalizedModelName);
                odds.put(entity, similarity);
            }

        }

        if (odds.isEmpty()) {
            return null;
        }

        var maxEntry = odds.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue()).orElse(null);

        return maxEntry != null ? maxEntry.getKey() : entities.stream().filter(e -> find(e, normalizedProviderName)).findFirst().orElse(null);
    }

    protected String normalizeName(final String name) {
        if (name == null) {
            return "";
        }

        return Normalizer.normalize(name.trim()
                .toLowerCase(), Normalizer.Form.NFD);
    }

    /**
     * Calculate the similarity between two strings
     * @param str1
     * @param str2
     * @return
     */
    public double calculateSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0;
        }

        String[] words1 = str1.split("\\s+");
        String[] words2 = str2.split("\\s+");

        Set<String> uniqueWords = new HashSet<>();
        int commonWordsCount = 0;

        for (String word : words1) {
            uniqueWords.add(word);
        }

        for (String word : words2) {
            if (uniqueWords.add(word) == false) {
                commonWordsCount++;
            }
        }

        if (uniqueWords.isEmpty()) {
            return 0;
        }

        return (double) commonWordsCount / uniqueWords.size() * 100;
    }

    public String calculateProximity(String base , List<String> testString, int maxDistance){
        if(base==null)
            throw new NullPointerException("Base String is required.");

        String result = null;
        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();
        int distance = Integer.MAX_VALUE;
        List<String> collect = testString.stream()
                .filter(s -> s!= null)
                .distinct()
                .map(s->s.trim().toLowerCase())
                .collect(Collectors.toList());

        for(String s : collect ){
            int diff = levenshteinDistance.apply(base.toLowerCase(), s.toLowerCase());
            if( diff < distance){
                result=s;
                distance=diff;
            }
        }

        if(distance>maxDistance){
            return null;
        }

        return result;
    }

    public Map<String,String> calculateProximity(List<String> bases , List<String> testString){
        final Map<String,String> result = new HashMap<>();
        bases.stream().forEach(s-> {
            result.put(s,calculateProximity(s,testString, 3));
        });
        return result;
    }


    /**
     *
     * @param testString
     * @param maxDistance
     * @return
     */
    public Map<String,String> calculateProximity(List<String> bases , List<String> testString, int maxDistance){
        final Map<String,String> result = new HashMap<>();
        bases.stream().forEach(s-> {
            result.put(s,calculateProximity(s,testString, maxDistance));
        });
        return result;
    }
}
