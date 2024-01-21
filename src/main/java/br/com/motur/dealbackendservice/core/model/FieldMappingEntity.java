package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.DataType;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "field_mapping")
public class FieldMappingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @Column(nullable = false, name = "local_field_name", length = 30)
    private String localFieldName;

    @Column(nullable = false, name = "external_field_name", length = 30)
    private String externalFieldName;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, name = "data_type")
    private DataType dataType;

}
