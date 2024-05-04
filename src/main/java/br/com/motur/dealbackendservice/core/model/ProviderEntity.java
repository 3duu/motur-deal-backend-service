package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.ApiType;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Essa classe representa o um fornecedor de anúncios.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "provider")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderEntity implements Serializable {

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

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "auto_download_catalog", nullable = false)
    private Boolean autoDownloadCatalog;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderEntity that = (ProviderEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(url, that.url) &&
                apiType == that.apiType; //&&
                //Objects.equals(active, that.active) &&
                //Objects.equals(autoDownloadCatalog, that.autoDownloadCatalog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, url, apiType, active, autoDownloadCatalog);
    }

}
