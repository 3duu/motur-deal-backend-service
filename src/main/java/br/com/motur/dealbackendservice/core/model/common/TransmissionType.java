package br.com.motur.dealbackendservice.core.model.common;

import org.apache.commons.lang3.StringUtils;

public enum TransmissionType {
    MANUAL("Manual"),
    AUTOMATIC("Automatico"),
    SEMI_AUTOMATIC("Semi-Automatico"),
    CVT("CVT"), // Continuously Variable Transmission
    DUAL_CLUTCH("Dupla-Embreagem"),
    TIPTRONIC("Tiptronic"),
    DIRECT_SHIFT("Direct Shift"),
    ELECTRIC_VARIABLE("Eletrico Variavel"),
    HYBRID("Híbrido"),
    NONE(StringUtils.EMPTY); // Para veículos que não possuem transmissão (ex: alguns elétricos)

    private final String displayName;

    TransmissionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
