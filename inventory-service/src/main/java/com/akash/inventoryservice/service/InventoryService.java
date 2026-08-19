package com.akash.inventoryservice.service;

import com.akash.inventoryservice.document.InventoryLog;
import com.akash.inventoryservice.document.Product;
import com.akash.inventoryservice.dto.InventoryStatusEvent;
import com.akash.inventoryservice.dto.OrderCreatedEvent;
import com.akash.inventoryservice.kafka.InventoryStatusProducer;
import com.akash.inventoryservice.repository.InventoryLogRepository;
import com.akash.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final InventoryStatusProducer inventoryStatusProducer;

    public void processOrderCreated(OrderCreatedEvent event) {
        String failureReason = null;

        // Check every item in the order has enough stock before reserving anything.
        for (OrderCreatedEvent.OrderItemPayload item : event.getItems()) {
            Product product = productRepository.findByProductId(item.getProductId()).orElse(null);

            if (product == null) {
                failureReason = "Product not found: " + item.getProductId();
                break;
            }
            if (product.getAvailableQuantity() < item.getQuantity()) {
                failureReason = "Insufficient stock for " + item.getProductId()
                        + " (requested " + item.getQuantity() + ", available " + product.getAvailableQuantity() + ")";
                break;
            }
        }

        InventoryStatusEvent statusEvent = new InventoryStatusEvent();
        statusEvent.setOrderReference(event.getOrderReference());
        statusEvent.setTimestamp(LocalDateTime.now());

        if (failureReason != null) {
            statusEvent.setStatus("FAILED");
            statusEvent.setReason(failureReason);
            log.warn("Inventory check FAILED for order {}: {}", event.getOrderReference(), failureReason);
        } else {
            // All good — deduct stock for every item.
            for (OrderCreatedEvent.OrderItemPayload item : event.getItems()) {
                Product product = productRepository.findByProductId(item.getProductId()).orElseThrow();
                product.setAvailableQuantity(product.getAvailableQuantity() - item.getQuantity());
                productRepository.save(product);
            }
            statusEvent.setStatus("RESERVED");
            log.info("Inventory RESERVED for order {}", event.getOrderReference());
        }

        inventoryLogRepository.save(new InventoryLog(
                null, event.getOrderReference(), statusEvent.getStatus(),
                statusEvent.getReason(), LocalDateTime.now()));

        inventoryStatusProducer.publish(statusEvent);
    }
}
