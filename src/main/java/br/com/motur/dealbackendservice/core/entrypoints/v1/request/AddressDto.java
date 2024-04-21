package br.com.motur.dealbackendservice.core.entrypoints.v1.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto implements Serializable {

    private Long id;

    private String zipCode;

    private String street;

    private String number;

    private String complement;

    private Integer cityId;

    private Double latitude;

    private Double longitude;

    private String type;
}
