package br.com.motur.dealbackendservice.core.model.common;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public enum DataType {
    BYTE("byte", Byte.class), //0
    SHORT("short", Short.class), //1
    INT("int", Integer.class), //2
    LONG("long", Long.class), //3
    FLOAT("float", Float.class), //4
    DOUBLE("double", Double.class),//5
    BOOLEAN("boolean", Boolean.class),//6
    CHAR("char", Character.class),//7

    STRING("String", String.class),//8
    DATE("Date", Date.class),//9
    LOCAL_DATETIME("LocalDateTime", LocalDateTime.class),//10
    BIG_DECIMAL("BigDecimal", BigDecimal.class),//11
    LIST("List", List.class),//12

    JSON("JSON", JsonNode.class),//13

    ID("ID", Id.class),//14

    MAP("Map", HashMap.class);//15

    private final String displayName;

    private final Class<?> classType;

    DataType(String displayName, Class<?> classType) {
        this.displayName = displayName;
        this.classType = classType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<?> getClassType() {
        return classType;
    }
}
