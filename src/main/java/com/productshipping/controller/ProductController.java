package com.productshipping.controller;


import com.productshipping.kafka.KafkaConsumer;
import com.productshipping.kafka.KafkaProducer;
import com.productshipping.resource.ProductResource;
import com.productshipping.util.ErrorResponse;
import com.productshipping.util.Message;
import com.productshipping.model.ProductEntity;
import com.productshipping.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
@RequestMapping(value = "/")
public class ProductController {

    public static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    KafkaConsumer kafkaConsumer;

    @Value("${spring.kafka.consumer.groupId}")
    String kafkaGroupId;

    @Value("${productShipping.kafka.post.product}")
    String postProductTopic;

    @Value("${productShipping.kafka.put.product}")
    String putProductTopic;

    @Value("${productShipping.kafka.update.product}")
    String updateProductTopic;


    @GetMapping(value = "")
    public ResponseEntity<?> getAllByProductName(@RequestParam("page") int page, @RequestParam("size") int size,
                                                 @RequestParam(value = "sort", defaultValue = "productName,asc") String sort,
                                                 RepresentationModelAssembler modelAssembler, @RequestHeader("User-Agent") String userAgent) {
        logger.info("Fetching all products");
        Page<ProductEntity> product = null;
        try {
            product = productService.getAllByProductName(page, size, sort);
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            ErrorResponse.returnErrorResponse(e.getMessage());
        }
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.put(HttpHeaders.USER_AGENT, Arrays.asList(userAgent));
        CollectionModel<ProductResource> pagedModel = modelAssembler.toCollectionModel(product);
        return new ResponseEntity<CollectionModel>(pagedModel, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") int id) {
        logger.info("Fetching product with ID {}", id);
        ProductEntity product = null;
        try {
            product = kafkaConsumer.getProductEntityFromKafka(id);
            if (product.getProductId() == 0) product = productService.findById(id);
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            ErrorResponse.returnErrorResponse(e.getMessage());
        }
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> addProduct(@RequestBody ProductEntity productEntity) {
        logger.info(("Adding a new product"));
        CollectionModel<Message> messages = null;
        try {
            productService.addProduct(productEntity);
            List<Message> messageList = Message.getMessageList("Created new product", HttpStatus.CREATED);
            messages = CollectionModel.of(messageList);
            messages.add(linkTo(ProductController.class).withSelfRel());
            messages.add(linkTo(AddressController.class).withRel("address"));
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            ErrorResponse.returnErrorResponse(e.getMessage());
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    private ResponseEntity<?> putAndUpdateProduct(ProductEntity productEntity, int id, int mode) {
        logger.info("Process '{}' product", (mode == 0 ? "put" : "update"));
        CollectionModel<Message> messages = null;
        try {
            List<Message> messageList = null;
            ProductEntity product = productService.findById(id);
            if (product != null) {
                messageList = Message.getMessageList((mode == 0 ? "Put" : "Update") + " product process", HttpStatus.OK);
                productEntity.setProductId(id);
                if (mode != 0) productEntity.setProductId(product.getProductId());
                kafkaProducer.postProduct(((mode == 0) ? putProductTopic : updateProductTopic), kafkaGroupId, productEntity);
            } else {
                messageList = Message.getMessageList("Product Id" + id + " Not Found!", HttpStatus.BAD_REQUEST);
                messages = CollectionModel.of(messageList);
                messages.add(linkTo(ProductController.class).withSelfRel());
                return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
            }
            messages = CollectionModel.of(messageList);
            messages.add(linkTo(ProductController.class).slash(id).withSelfRel());
            messages.add(linkTo(AddressController.class).withRel("address"));
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            ErrorResponse.returnErrorResponse(e.getMessage());
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PatchMapping(value = "/{productId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateProduct(@PathVariable("productId") int productId, @RequestBody ProductEntity productEntity) {
        return putAndUpdateProduct(productEntity, productId, 1);
    }

    @PutMapping(value = "/{productId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> putProduct(@PathVariable("productId") int productId, @RequestBody ProductEntity productEntity) {
        return putAndUpdateProduct(productEntity, productId, 0);
    }

}