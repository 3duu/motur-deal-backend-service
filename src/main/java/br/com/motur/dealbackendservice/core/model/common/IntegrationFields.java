package br.com.motur.dealbackendservice.core.model.common;

import lombok.Getter;

import java.util.Date;

/**
 * Enum que representa os campos de integração
 */
@Getter
public enum IntegrationFields {

    ID("{id}", "identificador", Long.class),
    UID("{uid}", "identificador único", String.class),
    USER_ID("{userId}", "identificador do usuário", Long.class),
    TOKEN("{token}", "token", String.class),
    NAME("{name}", "nome", String.class),
    DESCRIPTION("{description}", "descrição", String.class),
    PRICE("{price}", "preço", Double.class),
    QUANTITY("{quantity}", "quantidade", Integer.class),
    CATEGORY_ID("{category_id}", "id da categoria", Long.class),
    BRAND_ID("{brandId}", "id da marca", Long.class),
    MODEL_ID("{modelId}", "id do modelo", Long.class),
    TRIM_ID("{trimId}", "id da versão", Long.class),
    YEAR("{year}", "ano", Integer.class),
    ZERO_KM("{zeroKm}", "zero km", Boolean.class),
    FUEL("{fuel}", "combustível", String.class),
    COLOR("{color}", "cor", String.class),
    KM("{km}", "quilometragem", Integer.class),
    STATE("{state}", "estado", String.class),
    CITY("{city}", "cidade", String.class),
    ZIP_CODE("{zipCode}", "CEP", String.class),
    DISTANCE("{distance}", "distância", Integer.class),
    LATITUDE("{latitude}", "latitude", Double.class),
    LONGITUDE("{longitude}", "longitude", Double.class),
    RADIUS("{radius}", "raio", Integer.class),
    PAGE("{page}", "página", Integer.class),
    PAGE_SIZE("{pageSize}", "tamanho da página", Integer.class),
    SORT("{sort}", "ordenação", String.class),
    ORDER("{order}", "ordem", String.class),
    START_DATE("{startDate}", "data de início", Date.class),
    END_DATE("{endDate}", "data de término", Date.class),
    START_TIME("{startTime}", "hora de início", Date.class),
    END_TIME("{endTime}", "hora de término", Date.class);

    private final String value;
    private final String description;
    private final Class<?> type;

    IntegrationFields(String value, String description, Class<?> type) {
        this.value = value;
        this.description = description;
        this.type = type;
    }

    public String getNormalizedValue() {
        return value.replace("{", "").replace("}", "");
    }

}
