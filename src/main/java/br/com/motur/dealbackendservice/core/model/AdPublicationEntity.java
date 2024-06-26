package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.converter.JsonNodeConverter;
import br.com.motur.dealbackendservice.core.model.common.PublishingStatus;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


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

    @ManyToOne(fetch = FetchType.EAGER)
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
    @Enumerated(EnumType.STRING)
    private PublishingStatus status;

    @Column(name = "publication_date")
    private Date publicationDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(name = "additional_info", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode additionalInfo;

    @Column(name = "return_data", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode returnData;

    @Column(name = "original_ad", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode originalAd;

    @Override
    public String toString() {
        return "AdPublicationEntity(id=" + this.getId() + ", externalId=" + this.getExternalId() + ", ad=" + this.getAd().getId() + ", provider=" + this.getProvider().getName() + ", providerTrimsEntity=" + this.getProviderTrimsEntity().getName() + ", planId=" + this.getPlanId() + ", status=" + this.getStatus() + ", publicationDate=" + this.getPublicationDate() + ", expirationDate=" + this.getExpirationDate() + ", additionalInfo=" + this.getAdditionalInfo() + ")";
    }

}

