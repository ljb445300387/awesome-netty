package com.anthonyzero.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author admin
 */
@Data
@Entity
@Table(name = "city")
@Accessors(chain = true)
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "district")
    private String district;

    @Column(name = "population")
    private Integer population;
}
