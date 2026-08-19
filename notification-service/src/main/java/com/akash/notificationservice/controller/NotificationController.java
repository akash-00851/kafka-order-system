package com.akash.notificationservice.controller;

import com.akash.notificationservice.document.Notification;
import com.akash.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @GetMapping("/order/{orderReference}")
    public List<Notification> getByOrderReference(@PathVariable String orderReference) {
        return notificationRepository.findByOrderReference(orderReference);
    }
}
