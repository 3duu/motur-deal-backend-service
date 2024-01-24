package br.com.motur.dealbackendservice.core.model;


import br.com.motur.dealbackendservice.core.converter.JsonNodeConverter;
import br.com.motur.dealbackendservice.core.converter.ReturnMappingConverter;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import br.com.motur.dealbackendservice.core.model.common.EndpointMethod;
import br.com.motur.dealbackendservice.core.model.common.ReturnMapping;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Data;

/**
 *
 */
@Data
@Entity
@Table(name = "EndpointConfig", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider_id", "category"})
})
public class EndpointConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;// Provider associado

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private EndpointCategory category;// Categoria do endpoint (veículos, anúncios, etc.)

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "method", nullable = false)
    @Enumerated(EnumType.STRING)
    private EndpointMethod method;// Método HTTP

    @Column(name = "payload", columnDefinition = "jsonb")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode payload;// Payload

    @Column(name = "headers", columnDefinition = "jsonb")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode  headers;// Headers

    @Column(name = "additional_params", columnDefinition = "jsonb")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode  additionalParams;// Parâmetros adicionais

    @Column(name = "return_mapping", columnDefinition = "jsonb")
    @Convert(converter = ReturnMappingConverter.class)
    private ReturnMapping returnMapping;// Dados de retorno
}

