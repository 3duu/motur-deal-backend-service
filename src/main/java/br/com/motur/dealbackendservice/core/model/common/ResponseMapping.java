package br.com.motur.dealbackendservice.core.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Essa classe representa o mapeamento de campos de uma resposta.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMapping implements java.io.Serializable {

    private List<Config> fieldMappings;
    @Data
    public static class Config implements java.io.Serializable {

        @Enumerated(EnumType.STRING)
        private DataType originDatatype;

        @Enumerated(EnumType.STRING)
        private DataType returnDatatype;

        private String originPath;

        @Enumerated(EnumType.STRING)
        private FieldMapping destination;

        private Config returns;
    }

    /**
     * Configuração de mapeamento de campos.
     */
    @Getter
    public enum FieldMapping {

        RETURNS("returns"), //Proximo nível (Config.returns)
        NAME("name"), //Mapeamento de nome ou descrição
        SYNONYMS("synonyms"), //Mapeamento de sinônimos
        EXTERNAL_ID("externalId"), //Mapeamento de ID
        PARENT_ID("parentId"), //Mapeamento de ID do pai
        CONTENT("content"), //Mapeamento de conteúdo completo
        TOKEN("token"), //Mapeamento de token
        USER("user"), //Mapeamento de username ou email
        PASSWORD("password"), //Mapeamento de senha
        VALUE("value"); //Mapeamento de valor

        private final String value;

        FieldMapping(String value) {
            this.value = value;
        }
    }
}
