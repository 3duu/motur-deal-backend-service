package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.converter.JsonNodeConverter;
import br.com.motur.dealbackendservice.core.model.common.AuthType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidade que representa a configuração de autenticação de um Provider.
 * ID e Referências: authId é um identificador único para cada configuração de autenticação. providerId é uma referência ao ID do provedor associado.
 * Tipo de Autenticação: authType armazena o tipo de autenticação (como "Basic", "OAuth2", "API Key").
 * Detalhes da Autenticação: details é uma coluna JSON que armazena os detalhes variáveis da configuração de autenticação. Aqui, utilizamos JsonNode do Jackson para representar um nó JSON, e um conversor personalizado (JsonNodeConverter) para mapear entre a representação JSON e a entidade Java.
 * Validação e Relacionamentos: Dependendo das regras de negócio, adicione anotações de validação e defina os relacionamentos apropriados com outras entidades.
 */
@Entity
@Data
@Table(name = "auth_config")
public class AuthConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private AuthType authType; // Tipo de autenticação (Basic, OAuth2, API Key, etc.)

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode details; // Detalhes da autenticação como um objeto JSON

}

/**
 * Exemplos de Dados Armazenados em "details":
 * Para OAuth2:
 *
 * json
 * Copy code
 * {
 *     "client_id": "abc123",
 *     "client_secret": "xyz789",
 *     "scopes": ["read", "write"],
 *     "token_url": "https://example.com/oauth/token"
 * }
 * Para API Key:
 *
 * json
 * Copy code
 * {
 *     "api_key": "some_unique_key"
 * }
 * Para Basic Auth:
 *
 * json
 * Copy code
 * {
 *     "username": "user",
 *     "password": "pass"
 * }
 */
