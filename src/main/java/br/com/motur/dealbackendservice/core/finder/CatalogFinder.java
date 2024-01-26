package br.com.motur.dealbackendservice.core.finder;


import br.com.motur.dealbackendservice.core.model.CatalogEntity;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CatalogFinder<T extends CatalogEntity> {



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

    public abstract boolean find(T providerCatalogEntity, String text);

    protected String normalizeName(final String name) {
        if (name == null) {
            return "";
        }

        return Normalizer.normalize(name.trim()
                .replaceAll("\\s+", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase(), Normalizer.Form.NFD);
    }
}
