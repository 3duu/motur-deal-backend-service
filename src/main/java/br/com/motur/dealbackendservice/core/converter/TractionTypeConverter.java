package br.com.motur.dealbackendservice.core.converter;

import br.com.motur.dealbackendservice.core.model.common.VehicleTractionType;
import org.springframework.stereotype.Component;

@Component
public class TractionTypeConverter {

    public VehicleTractionType categorizeTraction(String tipo) {
        tipo = tipo.toLowerCase();

        if (tipo.contains("dianteira") || tipo.contains("4x2") || tipo.contains("front wheel") || tipo.contains("fwd") || tipo.contains(VehicleTractionType.FRONT_WHEEL_DRIVE.getDisplayName().toLowerCase())) {
            return VehicleTractionType.FRONT_WHEEL_DRIVE;
        } else if (tipo.contains("traseira") && !tipo.contains("4x4") && !tipo.contains("integral") || tipo.contains("rear wheel") || tipo.contains("rwd") || tipo.contains(VehicleTractionType.REAR_WHEEL_DRIVE.getDisplayName().toLowerCase())) {
            return VehicleTractionType.REAR_WHEEL_DRIVE;
        } else if (tipo.contains("integral") || tipo.contains("quattro") || tipo.contains("permanente")
                || tipo.contains("all wheel") || tipo.contains("awd") || tipo.contains("full time") || tipo.contains(VehicleTractionType.ALL_WHEEL_DRIVE.getDisplayName().toLowerCase())) {
            return VehicleTractionType.ALL_WHEEL_DRIVE;
        } else if (tipo.contains("4x4") || tipo.contains("four wheel drive") || tipo.contains("four wheel")
                || tipo.contains("4wd") || tipo.contains("part time")
                || tipo.contains("quadra-drive") || tipo.contains("command-trac")
                || tipo.contains("easy select") || tipo.contains("super select")
                || tipo.contains(VehicleTractionType.FOUR_WHEEL_DRIVE.getDisplayName().toLowerCase())) {
            return VehicleTractionType.FOUR_WHEEL_DRIVE;
        }

        return null; // Padrão se nenhuma correspondência for encontrada
    }
}
