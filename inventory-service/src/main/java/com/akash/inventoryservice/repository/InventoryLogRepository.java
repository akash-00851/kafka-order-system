package com.akash.inventoryservice.repository;

import com.akash.inventoryservice.document.InventoryLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InventoryLogRepository extends MongoRepository<InventoryLog, String> {
}
