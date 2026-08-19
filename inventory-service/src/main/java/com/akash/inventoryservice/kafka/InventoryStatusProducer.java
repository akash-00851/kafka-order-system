package com.akash.inventoryservice.kafka;

import com.akash.inventoryservice.dto.InventoryStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryStatusProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.inventory-status}")
    private String inventoryStatusTopic;

    public void publish(InventoryStatusEvent event) {
        kafkaTemplate.send(inventoryStatusTopic, event.getOrderReference(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish InventoryStatusEvent for order {}: {}",
                                event.getOrderReference(), ex.getMessage(), ex);
                    } else {
                        log.info("Published InventoryStatusEvent [{}] for order {} to partition {} offset {}",
                                event.getStatus(), event.getOrderReference(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
