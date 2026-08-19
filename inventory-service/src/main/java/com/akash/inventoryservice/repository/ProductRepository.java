package com.akash.inventoryservice.repository;

import com.akash.inventoryservice.document.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findByProductId(String productId);
}
