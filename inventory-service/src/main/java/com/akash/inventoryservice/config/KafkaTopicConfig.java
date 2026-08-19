package com.akash.inventoryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.inventory-status}")
    private String inventoryStatusTopic;

    // Inventory Service is the producer/owner of this topic.
    @Bean
    public NewTopic inventoryStatusTopic() {
        return TopicBuilder.name(inventoryStatusTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
