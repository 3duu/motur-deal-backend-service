package br.com.motur.dealbackendservice.core.model;


import br.com.motur.dealbackendservice.core.converter.JsonNodeConverter;
import br.com.motur.dealbackendservice.core.converter.ReturnMappingConverter;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import br.com.motur.dealbackendservice.core.model.common.EndpointMethod;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

/**
 * Essa classe representa o uma configuração de endpoint de um fornecedor.
 */
@Data
@Entity
@Table(name = "endpoint_config", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider_id", "category"})
})
public class EndpointConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "execution_order", columnDefinition = "smallint", nullable = false)
    private Short executionOrder;// Ordem de execução

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

    @Column(name = "response_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DataType responseType;// Tipo de resposta

    @Column(name = "response_mapping", columnDefinition = "jsonb")
    @Convert(converter = ReturnMappingConverter.class)
    private ResponseMapping responseMapping;// Dados de retorno

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointConfigEntity that = (EndpointConfigEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(executionOrder, that.executionOrder) &&
                Objects.equals(provider, that.provider) &&
                category == that.category &&
                Objects.equals(url, that.url) &&
                method == that.method &&
                Objects.equals(payload, that.payload) &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(additionalParams, that.additionalParams) &&
                responseType == that.responseType &&
                Objects.equals(responseMapping, that.responseMapping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, executionOrder, provider, category, url, method, payload, headers, additionalParams, responseType, responseMapping);
    }
}

