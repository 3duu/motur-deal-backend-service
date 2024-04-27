package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

/**
 * Essa classe representa o uma publicação de um veículo. Ela é responsável por guardar informações sobre a publicação de um veículo que já foi publicado.
 */
@Entity
@Table(name = "ad_publication")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdPublicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private String externalId; // Id da publicação no fornecedor

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false, referencedColumnName = "id")
    private AdEntity ad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_trims_id", nullable = false)
    private ProviderTrimsEntity providerTrimsEntity;

    @Column(name = "plan_id")
    private String planId; //Id do plano de publicação selecionado

    @Column(name = "status")
    private String status;

    @Column(name = "publication_date")
    private Date publicationDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(columnDefinition = "JSON")
    private String additionalInfo;

}

