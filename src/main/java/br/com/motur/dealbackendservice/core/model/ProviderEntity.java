package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.ApiType;


import jakarta.persistence.*;
import lombok.Data;

/**
 * Essa classe representa o um fornecedor de anúncios.
 */
@Entity
@Data
@Table(name = "provider")
public class ProviderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "api_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ApiType apiType;

}
