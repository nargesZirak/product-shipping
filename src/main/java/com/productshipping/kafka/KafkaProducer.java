package com.productshipping.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.productshipping.model.AddressEntity;
import com.productshipping.model.ProductEntity;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void postProduct(String topic, String groupId, ProductEntity productEntity){
        try {
            logger.info("Sending data to kafka: '{}' with topic: '{}'", productEntity.getName(), topic);
            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send(topic, groupId, mapper.writeValueAsString(productEntity));
        } catch (Exception e) {
            logger.error("Error: '{}'", e.getMessage());
        }
    }

    public void postAddress(String topic, String groupId, AddressEntity addressEntity){
        try {
            logger.info("Sending data to kafka = '{}' with topic '{}'", addressEntity.getZipcode(), topic);
            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send(topic, groupId, mapper.writeValueAsString(addressEntity));
        } catch (Exception e) {
            logger.error("Error: '{}'", e.getMessage());
        }
    }

}
