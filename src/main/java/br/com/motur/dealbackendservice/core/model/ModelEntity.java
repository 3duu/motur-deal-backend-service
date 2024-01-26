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

    @Transient
    private String[] synonymsArray;

    @Transient
    public String[] getSynonymsArray() {

        if (this.synonymsArray != null) {
            return this.synonymsArray;
        }
        synonymsArray = this.synonym != null ? this.synonym.split(",") : new String[0];
        for (int i = 0; i < synonymsArray.length; i++) {
            synonymsArray[i] = synonymsArray[i].trim().toLowerCase();
        }
        return synonymsArray;
    }

    @Override
    public CatalogEntity getBaseParentCatalog() {
        return brand;
    }
}
