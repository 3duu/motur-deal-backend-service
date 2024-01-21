package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.ApiType;
import com.amazonaws.HttpMethod;
import feign.Request;
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

    @Column(name = "list_vehicles_endpoint")
    private String listVehiclesEndpoint;

    @Column(name = "list_vehicles_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod listVehiclesMethod;

    @Column(name = "vehicle_details_endpoint")
    private String vehicleDetailsEndpoint;

    @Column(name = "vehicle_details_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod vehicleDetailsMethod;

    @Column(name = "create_ad_endpoint")
    private String createAdEndpoint;

    @Column(name = "create_ad_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod createAdMethod;

    @Column(name = "update_ad_endpoint")
    private String updateAdEndpoint;

    @Column(name = "update_ad_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod updateAdMethod;

    @Column(name = "delete_ad_endpoint")
    private String deleteAdEndpoint;

    @Column(name = "delete_ad_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod deleteAdMethod;

    @Column(name = "ad_status_endpoint")
    private String adStatusEndpoint;

    @Column(name = "ad_status_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod adStatusMethod;

    @Column(name = "stats_endpoint")
    private String statsEndpoint;

    @Column(name = "stats_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod statsMethod;

}
