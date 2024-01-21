package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.core.model.common.BodyType;
import org.springframework.stereotype.Component;

@Component
public class BodyTypeConverter {

        public BodyType categorizeBodyType(String tipo) {
            tipo = tipo.toLowerCase();

            if (tipo.contains("sedan") || tipo.contains("sedã")) {
                return BodyType.SEDAN;
            } else if (tipo.contains("hatchback") || tipo.contains("hatch")) {
                return BodyType.HATCHBACK;
            }
            else if (tipo.contains("suv") || tipo.contains("utilitário esportivo") || tipo.contains("utilitario esportivo") || tipo.contains("utilitario") || tipo.contains(BodyType.SUV.getDisplayName().toLowerCase())) {
                return BodyType.SUV;
            } else if (tipo.contains("coupe") || tipo.contains("coupé") || tipo.contains(BodyType.COUPE.getDisplayName().toLowerCase())) {
                return BodyType.COUPE;
            } else if (tipo.contains("convertible") || tipo.contains("cabriolet") || tipo.contains(BodyType.CONVERTIBLE.getDisplayName().toLowerCase())) {
                return BodyType.CONVERTIBLE;
            } else if (tipo.contains("wagon") || tipo.contains("estate") || tipo.contains(BodyType.WAGON.getDisplayName().toLowerCase())) {
                return BodyType.WAGON;
            } else if (tipo.contains("pickup") || tipo.contains("truck") || tipo.contains(BodyType.PICKUP.getDisplayName().toLowerCase())) {
                return BodyType.PICKUP;
            } else if (tipo.contains("van") || tipo.contains("minibus") || tipo.contains(BodyType.VAN.getDisplayName().toLowerCase())) {
                return BodyType.VAN;
            } else if (tipo.contains("minivan") || tipo.contains("mpv") || tipo.contains(BodyType.MINIVAN.getDisplayName().toLowerCase())) {
                return BodyType.MINIVAN;
            } else if (tipo.contains("sports car") || tipo.contains("sportscar") || tipo.contains(BodyType.SPORTS_CAR.getDisplayName().toLowerCase())) {
                return BodyType.SPORTS_CAR;
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
