package br.com.motur.dealbackendservice.core.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReturnMapping implements java.io.Serializable {

    private List<Config> fieldMappings;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config implements java.io.Serializable {

        private String originPath;

        @Enumerated(EnumType.STRING)
        private FieldMapping destination;

        @Enumerated(EnumType.STRING)
        private DataType originDatatype;
    }

    public enum FieldMapping {

        NAME("name"),
        SYNONYMS("synonyms"),
        EXTERNAL_ID("externalId");

        private final String value;

        FieldMapping(String value) {
            this.value = value;
        }
    }
}
