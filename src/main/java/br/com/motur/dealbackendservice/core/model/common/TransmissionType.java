package br.com.motur.dealbackendservice.core.model.common;

public enum TransmissionType {
    MANUAL("Manual"),
    AUTOMATIC("Automatic"),
    SEMI_AUTOMATIC("Semi-Automatic"),
    CVT("CVT"), // Continuously Variable Transmission
    DUAL_CLUTCH("Dual-Clutch"),
    TIPTRONIC("Tiptronic"),
    DIRECT_SHIFT("Direct Shift"),
    ELECTRIC_VARIABLE("Electric Variable"),
    HYBRID("Hybrid"),
    NONE("None"); // Para veículos que não possuem transmissão (ex: alguns elétricos)

    private final String displayName;

    TransmissionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
