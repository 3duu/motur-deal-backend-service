package br.com.motur.dealbackendservice.core.entrypoints.v1.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DealerDto implements java.io.Serializable {

    private Long id;

    private String name;

    private String cnpj;

    private AddressDto address;

    private String phone;

    private String email;

    private String status;

    private List<Integer> providersIds;
}
