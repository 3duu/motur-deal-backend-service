package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.ApiType;


import br.com.motur.dealbackendservice.core.model.common.EndpointMethod;
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

    @Column(name = "api_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ApiType apiType;
/*
    @Column(name = "list_vehicles_endpoint")
    private String listVehiclesEndpoint;

    @Column(name = "list_vehicles_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod listVehiclesMethod;

    @Column(name = "vehicle_details_endpoint")
    private String vehicleDetailsEndpoint;

    @Column(name = "vehicle_details_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod vehicleDetailsMethod;

    @Column(name = "create_ad_endpoint")
    private String createAdEndpoint;

    @Column(name = "create_ad_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod createAdMethod;

    @Column(name = "update_ad_endpoint")
    private String updateAdEndpoint;

    @Column(name = "update_ad_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod updateAdMethod;

    @Column(name = "delete_ad_endpoint")
    private String deleteAdEndpoint;

    @Column(name = "delete_ad_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod deleteAdMethod;

    @Column(name = "ad_status_endpoint")
    private String adStatusEndpoint;

    @Column(name = "ad_status_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod adStatusMethod;

    @Column(name = "stats_endpoint")
    private String statsEndpoint;

    @Column(name = "stats_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod statsMethod;

    // Endpoints de catálogo
    @Column(name = "catalog_brands_endpoint")
    private String catalogBrandsEndpoint;

    @Column(name = "catalog_brands_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod catalogBrandsMethod;

    @Column(name = "catalog_models_endpoint")
    private String catalogModelsEndpoint;

    @Column(name = "catalog_models_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod catalogModelsMethod;

    @Column(name = "catalog_versions_endpoint")
    private String catalogVersionsEndpoint;

    @Column(name = "catalog_versions_method")
    @Enumerated(EnumType.STRING)
    private EndpointMethod catalogVersionsMethod;*/

}
