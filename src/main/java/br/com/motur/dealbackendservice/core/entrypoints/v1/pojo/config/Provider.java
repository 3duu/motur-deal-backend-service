package br.com.motur.dealbackendservice.core.entrypoints.v1.pojo.config;

import br.com.motur.dealbackendservice.core.model.common.ApiType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonIgnoreProperties
@Data
@AllArgsConstructor
public class Provider {

    private Integer id;

    private String name;

    private String url;

    private ApiType apiType;
}
