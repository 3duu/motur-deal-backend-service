package br.com.motur.dealbackendservice.core.service.vo;

import br.com.motur.dealbackendservice.core.entrypoints.v1.pojo.config.Provider;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostResultsVo implements Serializable {

    private Long adId;
    private Map<Provider, Result> results = new HashMap<>();

    public void addError(String name, String message) {
        results.put(Provider.builder().name(name).build(), Result.builder().code("ERROR").status("ERROR").message(message).build());
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result implements Serializable {

        private String code;
        private String status;
        private String message;

    }
}
