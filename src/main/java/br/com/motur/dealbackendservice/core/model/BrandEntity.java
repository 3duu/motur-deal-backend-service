package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "brand")
public class BrandEntity implements CatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 25)
    private String name;

    @Column(name = "synonyms", length = 100)
    private String synonyms;

    @Column(name = "priority_order")
    private Integer order;

    private String[] synonymsArray;

    @Transient
    public String[] getSynonymsArray() {

        if (this.synonymsArray != null) {
            return this.synonymsArray;
        }
        synonymsArray = this.synonyms != null ? this.synonyms.split(",") : new String[0];
        for (int i = 0; i < synonymsArray.length; i++) {
            synonymsArray[i] = synonymsArray[i].trim().toLowerCase();
        }
        return synonymsArray;
    }
}
