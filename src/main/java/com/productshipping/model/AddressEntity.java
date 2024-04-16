package com.productshipping.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;


@Entity
@Table(name = "address_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "address_id")
    private long addressId;

    @Column(name = "line1")
    private String line1;

    @Column(name = "line2")
    private String lin2;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;
}
