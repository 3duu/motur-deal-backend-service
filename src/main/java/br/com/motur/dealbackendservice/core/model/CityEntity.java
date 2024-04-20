package br.com.motur.dealbackendservice.core.model;

import br.com.motur.dealbackendservice.core.model.common.BrazilianStates;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "city")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityEntity {

    @Id
    private Integer id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "uf", nullable = false)
    @Enumerated(EnumType.STRING)
    private BrazilianStates uf;
}
