package com.akash.notificationservice.kafka;

import com.akash.notificationservice.dto.InventoryStatusEvent;
import com.akash.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryStatusConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${app.kafka.topic.inventory-status}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleInventoryStatus(InventoryStatusEvent event) {
        log.info("Received InventoryStatusEvent for order {}: status={}",
                event.getOrderReference(), event.getStatus());

        try {
            notificationService.handleInventoryStatus(event);
        } catch (Exception e) {
            log.error("Failed to process notification for order {}: {}",
                    event.getOrderReference(), e.getMessage(), e);
        }
    }
}
