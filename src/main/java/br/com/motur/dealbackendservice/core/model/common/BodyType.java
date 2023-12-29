package br.com.motur.dealbackendservice.core.model.common;

public enum BodyType {
    SEDAN("Sedan"),
    HATCHBACK("Hatchback"),
    SUV("SUV"),
    COUPE("Coupe"),
    CONVERTIBLE("Convertible"),
    WAGON("Wagon"),
    PICKUP("Pickup"),
    VAN("Van"),
    MINIVAN("Minivan"),
    SPORTS_CAR("Sports Car");

    private final String displayName;

    BodyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
