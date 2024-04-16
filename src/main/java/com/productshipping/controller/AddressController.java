package com.productshipping.controller;


import com.productshipping.kafka.KafkaProducer;
import com.productshipping.model.AddressEntity;
import com.productshipping.service.AddressService;
import com.productshipping.util.ErrorResponse;
import com.productshipping.util.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping("/address")
public class AddressController {
    public static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    AddressService addressService;

    @Autowired
    KafkaProducer kafkaProducer;

    @Value("${spring.kafka.consumer.groupId}")
    String kafkaGroupId;

    @Value("${productShipping.kafka.post.address}")
    String postAddressTopic;

    @GetMapping(value = "")
    public ResponseEntity<?> getAllAddresses(){
        List<AddressEntity> addresses = null;
        try {
            addresses = addressService.getAddresses();
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            ErrorResponse.returnErrorResponse(e.getMessage());
        }
        CollectionModel<AddressEntity> addressCollectionModel = CollectionModel.of(addresses);
        addressCollectionModel.add(linkTo(AddressController.class).withSelfRel());
        addressCollectionModel.add(linkTo(ProductController.class).withRel("address"));
        return new ResponseEntity<>(addressCollectionModel, HttpStatus.OK);
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> addAddress(@RequestBody AddressEntity addressEntity){
        logger.info(("Process adding new address"));
        Message message = new Message();
        try {
            kafkaProducer.postAddress(postAddressTopic, kafkaGroupId, addressEntity);
            message.setStatusCode(HttpStatus.OK.value());
            message.setMessage("Created new address");
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            ErrorResponse.returnErrorResponse(e.getMessage());
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
