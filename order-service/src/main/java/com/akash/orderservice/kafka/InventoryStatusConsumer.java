package com.akash.orderservice.kafka;

import com.akash.orderservice.dto.InventoryStatusEvent;
import com.akash.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryStatusConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "${app.kafka.topic.inventory-status}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleInventoryStatus(InventoryStatusEvent event) {
        log.info("Received InventoryStatusEvent for order {}: status={}",
                event.getOrderReference(), event.getStatus());

        try {
            orderService.updateOrderStatusFromInventoryEvent(event);
        } catch (Exception e) {

            log.error("Failed to process InventoryStatusEvent for order {}: {}",
                    event.getOrderReference(), e.getMessage(), e);
        }
    }
}
