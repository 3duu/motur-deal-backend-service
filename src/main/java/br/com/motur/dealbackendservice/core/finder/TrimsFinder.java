package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.TrimEntity;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class TrimsFinder extends CatalogFinder<TrimEntity> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public boolean find(final TrimEntity trim, final String nameInProvider) {

        final String normalizedModelName = normalizeName(trim, trim.getName());
        final String normalizedProviderName = normalizeName(trim, nameInProvider);

        final boolean match = trim.getName().trim().toLowerCase().equals(nameInProvider.trim().toLowerCase()) || normalizedModelName.equals(normalizedProviderName)
                || ArrayUtils.contains(trim.getSynonymsArray(), nameInProvider.toLowerCase()) || ArrayUtils.contains(trim.getSynonymsArray(), normalizeName(null, nameInProvider));

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

    private String normalizeName(final TrimEntity model, final String name) {
        if (name == null) {
            return "";
        }

        return Normalizer.normalize(name.trim()
                //.replaceAll("\\s+", "")
                //.replaceAll("[^a-zA-Z0-9]", "") // TODO: Verificar se é necessário remover os caracteres especiais
                //.replace(model != null ? normalizeName(null, model.getModel().getName()) : "", "")
                .toLowerCase(), Normalizer.Form.NFD);
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


    public String extractModelName(final TrimEntity trim) {

        return normalizeName(trim.getModel().getName());
    }

    public String extractModelName(final String trimName) {

        return trimName.split(" ")[0];
    }

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

    boolean isAutomatic(String trimName, TrimEntity trim) {
        return (trimName.toLowerCase().contains(" automatico ") || trimName.toLowerCase().contains(" aut ") || trimName.toLowerCase().contains(" aut.")) && trim.getTransmissionType().equals(TransmissionType.AUTOMATIC);
    }
}
