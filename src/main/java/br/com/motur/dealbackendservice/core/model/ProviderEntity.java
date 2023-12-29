package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProviderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;

    private String name;
    private String url;
    private String apiType; // REST, SOAP, etc.
    private String authType; // Basic, OAuth2, API Key, etc.

}
