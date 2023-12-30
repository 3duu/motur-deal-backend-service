package br.com.motur.dealbackendservice.core.model.common;

public enum FuelType {

    GASOLINE("Gasoline"),

    FLEX("Flex"),

    ETHANOL("Ethanol"),

    DIESEL("Diesel"),

    ELECTRIC("Electric"),

    HYBRID("Hybrid"),

    BIOFUEL("Biofuel"),
    LPG("LPG"), // Liquefied Petroleum Gas

    CNG("CNG"), // Compressed Natural Gas

    HYDROGEN("Hydrogen"),

    OTHER("Outro");

    private final String displayName;

    FuelType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
