package com.akash.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private String orderReference;
    private String customerName;
    private String customerEmail;
    private Double totalAmount;
    private List<OrderItemPayload> items;
    private LocalDateTime timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemPayload {
        private String productId;
        private String productName;
        private Integer quantity;
        private Double price;
    }
}
