package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.converter.JsonNodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;

/**
 * Entidade que representa a configuração de autenticação de um Provider.
 * ID e Referências: authId é um identificador único para cada configuração de autenticação. providerId é uma referência ao ID do provedor associado.
 * Tipo de Autenticação: authType armazena o tipo de autenticação (como "Basic", "OAuth2", "API Key").
 * Detalhes da Autenticação: details é uma coluna JSON que armazena os detalhes variáveis da configuração de autenticação. Aqui, utilizamos JsonNode do Jackson para representar um nó JSON, e um conversor personalizado (JsonNodeConverter) para mapear entre a representação JSON e a entidade Java.
 * Validação e Relacionamentos: Dependendo das regras de negócio, adicione anotações de validação e defina os relacionamentos apropriados com outras entidades.
 */
@Entity
public class AuthConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @Column(nullable = false)
    private Long providerId; // Referência ao ID do Provider

    @Column(length = 50, nullable = false)
    private String authType; // Tipo de autenticação (Basic, OAuth2, API Key, etc.)

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode details; // Detalhes da autenticação como um objeto JSON

}
