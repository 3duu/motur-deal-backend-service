package br.com.motur.dealbackendservice.core.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Table(name = "address")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "zip_code", length = 24, nullable = false)
    private String zipCode;

    @Column(name = "street", length = 150, nullable = false)
    private String street;

    @Column(name = "number", length = 10, nullable = false)
    private String number;

    @Column(name = "complement", length = 150)
    private String complement;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", nullable=false, referencedColumnName = "id")
    private CityEntity city;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "type", length = 50)
    private String type;
}
