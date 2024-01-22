package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "brand")
public class BrandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 25)
    private String name;

    @Column(name = "synonyms", length = 100)
    private String synonyms;

    @Column(name = "priority_order")
    private Integer order;

    @Transient
    public String[] getSynonymsArray() {
        return synonyms.split(",");
    }
}
