package br.com.motur.dealbackendservice.core.model.common;

public enum VehicleTractionType {
    FRONT_WHEEL_DRIVE("Tração Dianteira"),
    REAR_WHEEL_DRIVE("Tração Traseira"),
    ALL_WHEEL_DRIVE("Tração nas Quatro Rodas"),
    FOUR_WHEEL_DRIVE("Tração Integral");

    private final String displayName;

    VehicleTractionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

