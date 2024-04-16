package com.productshipping.service;

import com.productshipping.model.ProductEntity;
import com.productshipping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service("ProductService")
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;


    public ProductEntity findById(int id){
        return productRepository.findById(id);
    }

    @Transactional
    public ProductEntity addProduct(ProductEntity productEntity) {
        productEntity.setProductId(productRepository.getNextSeriesId().intValue());
        return productRepository.save(productEntity);
    }

    @Transactional
    public ProductEntity updateProduct(ProductEntity productEntity) {
        return productRepository.save(productEntity);
    }

}
