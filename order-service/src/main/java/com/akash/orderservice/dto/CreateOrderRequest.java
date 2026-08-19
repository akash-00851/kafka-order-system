package com.akash.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "customerName is required")
    private String customerName;

    @Email(message = "customerEmail must be valid")
    @NotBlank(message = "customerEmail is required")
    private String customerEmail;

    @NotEmpty(message = "items cannot be empty")
    @Valid
    private List<OrderItemRequest> items;
}
