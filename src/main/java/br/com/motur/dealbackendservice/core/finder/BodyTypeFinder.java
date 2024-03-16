package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.common.BodyType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BodyTypeFinder {

    final Map<String, BodyType> bodyTypeMap = new HashMap<>();

    public BodyTypeFinder() {
        bodyTypeMap.put("sedan", BodyType.SEDAN);
        bodyTypeMap.put("sedã", BodyType.SEDAN);
        bodyTypeMap.put("hatchback", BodyType.HATCHBACK);
        bodyTypeMap.put("hatch", BodyType.HATCHBACK);
        bodyTypeMap.put("suv", BodyType.SUV);
        bodyTypeMap.put("utilitário esportivo", BodyType.SUV);
        bodyTypeMap.put("utilitario esportivo", BodyType.SUV);
        bodyTypeMap.put("utilitario", BodyType.SUV);
        bodyTypeMap.put(BodyType.SUV.getDisplayName().toLowerCase(), BodyType.SUV);
        bodyTypeMap.put("coupe", BodyType.COUPE);
        bodyTypeMap.put("coupé", BodyType.COUPE);
        bodyTypeMap.put(BodyType.COUPE.getDisplayName().toLowerCase(), BodyType.COUPE);
        bodyTypeMap.put("convertible", BodyType.CONVERTIBLE);
        bodyTypeMap.put("cabriolet", BodyType.CONVERTIBLE);
        bodyTypeMap.put(BodyType.CONVERTIBLE.getDisplayName().toLowerCase(), BodyType.CONVERTIBLE);
        bodyTypeMap.put("wagon", BodyType.WAGON);
        bodyTypeMap.put("estate", BodyType.WAGON);
        bodyTypeMap.put(BodyType.WAGON.getDisplayName().toLowerCase(), BodyType.WAGON);
        bodyTypeMap.put("pickup", BodyType.PICKUP);
        bodyTypeMap.put("truck", BodyType.PICKUP);
        bodyTypeMap.put(BodyType.PICKUP.getDisplayName().toLowerCase(), BodyType.PICKUP);
        bodyTypeMap.put("van", BodyType.VAN);
        bodyTypeMap.put("minibus", BodyType.VAN);
        bodyTypeMap.put(BodyType.VAN.getDisplayName().toLowerCase(), BodyType.VAN);
        bodyTypeMap.put("minivan", BodyType.MINIVAN);
        bodyTypeMap.put("mpv", BodyType.MINIVAN);
        bodyTypeMap.put(BodyType.MINIVAN.getDisplayName().toLowerCase(), BodyType.MINIVAN);
        bodyTypeMap.put("sports car", BodyType.SPORTS_CAR);
        bodyTypeMap.put("sportscar", BodyType.SPORTS_CAR);
        bodyTypeMap.put(BodyType.SPORTS_CAR.getDisplayName().toLowerCase(), BodyType.SPORTS_CAR);
    }


    public BodyType categorizeBodyType(String tipo) {
        tipo = tipo.toLowerCase();

        for (Map.Entry<String, BodyType> entry : bodyTypeMap.entrySet()) {
            if (tipo.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null; // Padrão se nenhuma correspondência for encontrada
    }

    public BodyType categorizeBodyType(Integer id) {

        if (id == 1){
            return BodyType.SEDAN;
        } else if (id == 2) {
            return BodyType.HATCHBACK;
        } else if (id == 6) {
            return BodyType.SUV;
        } /*else if (tipo.contains("coupe") || tipo.contains("coupé")) {
            return BodyType.COUPE;
        }*/ else if (id == 3) {
            return BodyType.CONVERTIBLE;
        } else if (id == 5) {
            return BodyType.WAGON;
        } else if (id == 7) {
            return BodyType.PICKUP;
        } else if (id == 4) {
            return BodyType.VAN;
        } else if (id == 8) {
            return BodyType.MINIVAN;
        } else if (id == 20) {
            return BodyType.SPORTS_CAR;
        }

        return null; // Padrão se nenhuma correspondência for encontrada
    }

}
