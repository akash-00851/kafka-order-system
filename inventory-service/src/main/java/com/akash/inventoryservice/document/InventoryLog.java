package com.akash.inventoryservice.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "inventory_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLog {

    @Id
    private String id;

    private String orderReference;

    private String result; // RESERVED or FAILED

    private String reason;

    private LocalDateTime processedAt;
}
