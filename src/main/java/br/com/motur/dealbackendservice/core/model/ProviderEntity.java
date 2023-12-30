package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.ApiType;
import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name = "api_type")
    @Enumerated(EnumType.STRING)
    private ApiType apiType;

    @ManyToOne
    @JoinColumn(name = "auth_config_id")
    private AuthConfigEntity authConfig;

    // Campos para mapeamento equivalente com Vehicle
    private String vehicleIdFieldName;  // Mapeia para id em Vehicle
    private String vehicleBrandFieldName; // Mapeia para brandId em Vehicle
    private String vehicleModelFieldName; // Mapeia para modelId em Vehicle
    private String vehicleTrimFieldName; // Mapeia para trimId em Vehicle
    private String vehicleModelYearFieldName; // Mapeia para modelYear em Vehicle
    private String vehicleProductionYearFieldName; // Mapeia para productionYear em Vehicle
    private String vehicleFuelTypeFieldName; // Mapeia para fuelType em Vehicle
    private String vehicleTransmissionTypeFieldName; // Mapeia para transmissionType em Vehicle
    private String vehicleColorFieldName; // Mapeia para color em Vehicle
    private String vehicleLicensePlateFieldName; // Mapeia para licensePlate em Vehicle
    private String vehicleKmFieldName; // Mapeia para km em Vehicle
    private String vehiclePriceFieldName; // Mapeia para price em Vehicle
    private String vehicleDescriptionFieldName; // Mapeia para description em Vehicle
    private String vehicleDealerFieldName; // Mapeia para dealerId em Vehicle
    private String vehicleStatusFieldName; // Mapeia para status em Vehicle
    private String vehicleTypeFieldName; // Mapeia para vehicleType em Vehicle
    private String vehicleBodyTypeFieldName; // Mapeia para bodyType em Vehicle

}
