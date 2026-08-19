package com.akash.notificationservice.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;

    private String orderReference;

    private String channel; // EMAIL

    private String message;

    private String status; // SENT or FAILED_ORDER

    private LocalDateTime sentAt;
}
