package com.productshipping.repository;


import com.productshipping.model.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(path = "/api/address")
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    @Query(value = "SELECT nextval('Address_id_seq')", nativeQuery = true)
    Long getNextSeriesId();
}
