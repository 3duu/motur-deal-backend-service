package br.com.motur.dealbackendservice.core.entrypoints.v1.request;

import br.com.motur.dealbackendservice.core.model.AddressEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DealerDto implements java.io.Serializable {

    private Long id;

    private String name;

    private String cnpj;

    private AddressEntity address;

    private String phone;

    private String email;

    private String status;
}
