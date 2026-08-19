package com.akash.notificationservice.service;

import com.akash.notificationservice.document.Notification;
import com.akash.notificationservice.dto.InventoryStatusEvent;
import com.akash.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void handleInventoryStatus(InventoryStatusEvent event) {
        String message;
        String status;

        if ("RESERVED".equalsIgnoreCase(event.getStatus())) {
            message = "Good news! Your order " + event.getOrderReference()
                    + " has been confirmed and is being prepared for shipment.";
            status = "SENT";
        } else {
            message = "Sorry, your order " + event.getOrderReference()
                    + " could not be processed. Reason: " + event.getReason();
            status = "SENT";
        }


        log.info("EMAIL To: customer of order {} | Message: {}",
                event.getOrderReference(), message);

        Notification notification = new Notification(
                null,
                event.getOrderReference(),
                "EMAIL",
                message,
                status,
                LocalDateTime.now()
        );

        notificationRepository.save(notification);
    }
}
