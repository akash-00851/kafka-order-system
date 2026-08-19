package com.akash.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatusEvent {

    private String orderReference;
    private String status; // "RESERVED" or "FAILED"
    private String reason;
    private LocalDateTime timestamp;
}
