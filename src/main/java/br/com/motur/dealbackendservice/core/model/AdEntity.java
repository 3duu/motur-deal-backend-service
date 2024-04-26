package br.com.motur.dealbackendservice.core.model;


import br.com.motur.dealbackendservice.common.FieldMappingInfo;
import br.com.motur.dealbackendservice.core.converter.BrandCatalogHelper;
import br.com.motur.dealbackendservice.core.converter.ModelCatalogHelper;
import br.com.motur.dealbackendservice.core.converter.TitleHelper;
import br.com.motur.dealbackendservice.core.converter.TrimCatalogHelper;
import br.com.motur.dealbackendservice.core.model.common.Color;
import br.com.motur.dealbackendservice.core.model.common.DataType;
import br.com.motur.dealbackendservice.core.model.common.FuelType;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * Essa entidade representa um anúncio local de um veículo.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ad")
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @FieldMappingInfo(name = "brand", type = DataType.INT, helper = BrandCatalogHelper.class)
    @Column(name = "brand_id")
    private Integer brandId; // Id da marca

    @FieldMappingInfo(name = "model", type = DataType.INT, helper = ModelCatalogHelper.class)
    @Column(name = "model_id")
    private Integer modelId; // Id do modelo

    @FieldMappingInfo(name = "trim", type = DataType.INT, helper = TrimCatalogHelper.class)
    @Column(name = "trim_id")
    private Integer trimId; // Id da versão

    @FieldMappingInfo(name = "modelYear", type = DataType.INT)
    @Column(name = "model_year")
    private Integer modelYear; // Ano do modelo

    @FieldMappingInfo(name = "productionYear", type = DataType.INT)
    @Column(name = "production_year")
    private Integer productionYear; // Ano de produção

    @FieldMappingInfo(name = "fuelId", type = DataType.INT)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;  // Tipo de combustível

    @FieldMappingInfo(name = "transmissionType", type = DataType.STRING)
    @Column(name = "transmission_id")
    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @FieldMappingInfo(name = "licensePlate", type = DataType.STRING)
    @Column(name = "license_plate")
    private String licensePlate; // Placa

    @FieldMappingInfo(name = "color", type = DataType.INT)
    @Column(name = "color_id")
    @Enumerated(EnumType.STRING)
    private Color color; // Id da cor

    @FieldMappingInfo(name = "km", type = DataType.INT)
    @Column(name = "km")
    private Integer km; // Quilometragem

    @FieldMappingInfo(name = "price", type = DataType.DOUBLE)
    @Column(name = "price")
    private BigDecimal price; // Preço

    @FieldMappingInfo(name = "mileage", type = DataType.INT)
    @Column(name = "mileage")
    private Integer mileage; // Quilometragem

    @FieldMappingInfo(name = "description", type = DataType.STRING)
    @Column(name = "description")
    private String description; // Descrição

    @FieldMappingInfo(name = "tile", type = DataType.STRING, helper = TitleHelper.class)
    @Column(name = "title")
    private String title; // Descrição

    @FieldMappingInfo(name = "dealer", type = DataType.ID)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private DealerEntity dealer; // Id da concessionária

    @Column(name = "status")
    private String status; // Status do veículo

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ad", cascade = CascadeType.ALL)
    private List<AdPublicationEntity> adPublicationEntityList;

}
