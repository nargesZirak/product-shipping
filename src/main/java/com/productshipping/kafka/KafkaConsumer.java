package com.productshipping.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productshipping.model.AddressEntity;
import com.productshipping.model.ProductEntity;
import com.productshipping.service.AddressService;
import com.productshipping.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;

@Component
@Transactional
public class KafkaConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    ProductService productService;

    @Autowired
    AddressService addressService;

    private ProductEntity kafkaProductEntity = new ProductEntity();

    @KafkaListener(topics = "4igc0qsg-productShipping.kafka.post.product", groupId = "productShipping")
    public void processPostProduct(String product){
        logger.info("received content: '{}'", product);
        try{
            ObjectMapper mapper = new ObjectMapper();
            ProductEntity productEntity = mapper.readValue(product, ProductEntity.class);
            ProductEntity productEntity1 = productService.addProduct(productEntity);
            logger.info("Success process product: '{}' with topic: '{}'", productEntity1.getName(), "productShipping.kafka.post.product");
        } catch (Exception e){
            logger.error("Error: '{}'", e.getMessage());
        }
    }

    @KafkaListener(topics = "4igc0qsg-productShipping.kafka.put.product" , groupId = "productShipping")
    public void processPutProduct(String product){
        logger.info("received content = '{}'", product);
        try{
            ObjectMapper mapper = new ObjectMapper();
            ProductEntity productEntity = mapper.readValue(product, ProductEntity.class);
            kafkaProductEntity = productEntity;
            logger.info("Success process product: '{}' with topic: '{}'", productEntity.getName(), "productShipping.kafka.put.product");
        } catch (Exception e){
            logger.error("Error: '{}'", e.getMessage());
        }
    }

    @KafkaListener(topics = "4igc0qsg-productShipping.kafka.update.product", groupId = "productShipping")
    public void processUpdateProduct(String product){
        logger.info("received content: '{}'", product);
        try{
            ObjectMapper mapper = new ObjectMapper();
            ProductEntity productEntity = mapper.readValue(product, ProductEntity.class);
            ProductEntity productEntity1 = productService.updateProduct(productEntity);
            logger.info("Success process product: '{}' with topic: '{}'", productEntity1.getName(), "productShipping.kafka.update.product");
        } catch (Exception e){
            logger.error("Error: '{}'", e.getMessage());
        }
    }

    @KafkaListener(topics = "4igc0qsg-productShipping.kafka.post.product.address", groupId = "productShipping")
    public void processPostAddress(String address){
        logger.info("received content: '{}'", address);
        try{
            ObjectMapper mapper = new ObjectMapper();
            AddressEntity addressEntity = mapper.readValue(address, AddressEntity.class);
            AddressEntity addressEntity1 = addressService.addAddress(addressEntity);
            logger.info("Success process address: '{}' with topic: '{}'", addressEntity1.getZipcode(), "productShipping.kafka.post.address");
        } catch (Exception e){
            logger.error("Error: '{}'", e.getMessage());
        }
    }

    public ProductEntity getProductEntityFromKafka(int id){
        return kafkaProductEntity;
    }

}