package com.productshipping.service;

import com.productshipping.model.ProductEntity;


public interface ProductService {
    ProductEntity addProduct(ProductEntity productEntity);
    ProductEntity findById(int id);
    ProductEntity updateProduct(ProductEntity productEntity);
}
