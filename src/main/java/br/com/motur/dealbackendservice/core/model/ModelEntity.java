package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "model")
public class ModelEntity implements CatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "synonym")
    private String synonym;

    @Column(name = "search_index")
    private Float searchIndex;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    @Override
    public String[] getSynonymsArray() {
        return new String[0];
    }

    @Override
    public CatalogEntity getBaseParentCatalog() {
        return brand;
    }
}
