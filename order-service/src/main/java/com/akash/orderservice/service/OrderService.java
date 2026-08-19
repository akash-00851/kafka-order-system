package com.akash.orderservice.service;

import com.akash.orderservice.dto.CreateOrderRequest;
import com.akash.orderservice.dto.InventoryStatusEvent;
import com.akash.orderservice.dto.OrderCreatedEvent;
import com.akash.orderservice.dto.OrderItemRequest;
import com.akash.orderservice.entity.Order;
import com.akash.orderservice.entity.OrderItem;
import com.akash.orderservice.entity.OrderStatus;
import com.akash.orderservice.exception.OrderNotFoundException;
import com.akash.orderservice.kafka.OrderEventProducer;
import com.akash.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());

        List<OrderItem> items = request.getItems().stream()
                .map(this::toOrderItem)
                .collect(Collectors.toList());

        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        double total = items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        log.info("Order {} created with status {}", saved.getOrderReference(), saved.getStatus());

        // Publish event AFTER the DB commit succeeds.
        orderEventProducer.publishOrderCreated(toOrderCreatedEvent(saved));

        return saved;
    }

    @Transactional
    public void updateOrderStatusFromInventoryEvent(InventoryStatusEvent event) {
        Order order = orderRepository.findByOrderReference(event.getOrderReference())
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found for reference: " + event.getOrderReference()));

        if ("RESERVED".equalsIgnoreCase(event.getStatus())) {
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            order.setStatus(OrderStatus.FAILED);
        }

        orderRepository.save(order);
        log.info("Order {} status updated to {}", order.getOrderReference(), order.getStatus());
    }

    public Order getOrderByReference(String orderReference) {
        return orderRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found for reference: " + orderReference));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private OrderItem toOrderItem(OrderItemRequest req) {
        OrderItem item = new OrderItem();
        item.setProductId(req.getProductId());
        item.setProductName(req.getProductName());
        item.setQuantity(req.getQuantity());
        item.setPrice(req.getPrice());
        return item;
    }

    private OrderCreatedEvent toOrderCreatedEvent(Order order) {
        List<OrderCreatedEvent.OrderItemPayload> payloadItems = order.getItems().stream()
                .map(i -> new OrderCreatedEvent.OrderItemPayload(
                        i.getProductId(), i.getProductName(), i.getQuantity(), i.getPrice()))
                .collect(Collectors.toList());

        return new OrderCreatedEvent(
                order.getOrderReference(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                payloadItems,
                LocalDateTime.now()
        );
    }
}
