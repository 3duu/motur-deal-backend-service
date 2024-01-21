package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_publication")
@Data
public class VehiclePublicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private String externalId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @Column(name = "plan_id")
    private String planId;

    @Column(name = "status")
    private String status;

    @Column(name = "publication_date")
    private Date publicationDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(columnDefinition = "JSON")
    private String additionalInfo;

}

