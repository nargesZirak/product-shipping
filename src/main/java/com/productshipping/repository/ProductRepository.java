package com.productshipping.repository;


import com.productshipping.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    ProductEntity findById(int id);

    @Query(value = "SELECT nextval('product_id_seq')", nativeQuery = true)
    Long getNextSeriesId();
}
