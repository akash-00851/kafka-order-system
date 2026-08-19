package com.akash.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class OrderItemRequest {

    @NotBlank(message = "productId is required")
    private String productId;

    @NotBlank(message = "productName is required")
    private String productName;

    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    private Double price;
}
