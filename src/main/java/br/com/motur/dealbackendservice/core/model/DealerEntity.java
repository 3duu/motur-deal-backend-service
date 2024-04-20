package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;
import java.util.List;

/**
 * Essa classe representa uma revenda de veículos.
 */
@Data
@Entity
@Table(name = "dealer")
public class DealerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phoneNumber")
    private String phoneNumber; // Telefone de Contato

    @Column(name = "email")
    private String email; // Email

    @Column(name = "website")
    private String website; // Website

    @Column(name = "logoUrl")
    private String logoUrl; // URL do Logotipo

    @Column(name = "description")
    private String description; // Descrição

    @Column(name = "openingHours")
    private String openingHours; // Horário de Funcionamento

    @Column(name = "unique_identifier")
    private String uniqueIdentifier; // Código Identificador Único

    @Column(name = "bank_details")
    private String bankDetails; // Informações Bancárias

    @Column(name = "legal_representative")
    private String legalRepresentative; // Responsável Legal / Gerente

    @Column(name = "latitude")
    private Double latitude; // Latitude

    @Column(name = "longitude")
    private Double longitude; // Longitude

    @Column(name = "status")
    private String status; // Status da Concessionária

    @Column(name = "date_added")
    private Date dateAdded; // Data de Cadastro

    @Column(name = "last_updated")
    private Date lastUpdated; // Data da Última Atualização

    @Column(name = "social_media_links")
    private String socialMediaLinks; // Informações de Redes Sociais

    @Column(name = "dealership_type")
    private String dealershipType; // Tipo de Concessionária

    @Column(name = "rating")
    private Double rating; // Avaliação/Classificação

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private AddressEntity addressEntity;

    @ManyToMany(
            fetch = FetchType.LAZY,
            targetEntity = ProviderEntity.class)
    @JoinTable(name = "dealer_provider",
            joinColumns = {@JoinColumn(name = "dealer_id")},
            inverseJoinColumns = {@JoinColumn(name = "provider_id")})
    @Fetch(FetchMode.SUBSELECT)
    private List<ProviderEntity> providers; // Fornecedores Associados



}
