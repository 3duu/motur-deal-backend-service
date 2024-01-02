package br.com.motur.dealbackendservice.core.entrypoints.v1.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class LoginRequest implements Serializable {
    private String username;
    private String password;
}
