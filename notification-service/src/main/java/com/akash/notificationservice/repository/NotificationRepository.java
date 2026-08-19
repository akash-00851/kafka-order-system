package com.akash.notificationservice.repository;

import com.akash.notificationservice.document.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByOrderReference(String orderReference);
}
