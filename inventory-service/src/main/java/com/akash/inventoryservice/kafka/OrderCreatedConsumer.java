package com.akash.inventoryservice.kafka;

import com.akash.inventoryservice.dto.OrderCreatedEvent;
import com.akash.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "${app.kafka.topic.order-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for order {} with {} item(s)",
                event.getOrderReference(), event.getItems().size());

        try {
            inventoryService.processOrderCreated(event);
        } catch (Exception e) {
            log.error("Failed to process OrderCreatedEvent for order {}: {}",
                    event.getOrderReference(), e.getMessage(), e);
        }
    }
}
