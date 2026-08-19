package com.akash.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddStockRequest {

    @NotBlank
    private String productId;

    @NotBlank
    private String productName;

    @Min(0)
    private Integer quantity;
}
