package com.akash.inventoryservice.controller;

import com.akash.inventoryservice.document.Product;
import com.akash.inventoryservice.dto.AddStockRequest;
import com.akash.inventoryservice.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;


    @PostMapping
    public ResponseEntity<Product> addStock(@Valid @RequestBody AddStockRequest request) {
        Product product = productRepository.findByProductId(request.getProductId())
                .orElse(new Product(null, request.getProductId(), request.getProductName(), 0));

        product.setProductName(request.getProductName());
        product.setAvailableQuantity(product.getAvailableQuantity() + request.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable String productId) {
        return productRepository.findByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
