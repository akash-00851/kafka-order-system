package com.akash.orderservice.controller;

import com.akash.orderservice.dto.CreateOrderRequest;
import com.akash.orderservice.entity.Order;
import com.akash.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order creation and lookup APIs")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order — publishes an OrderCreated Kafka event")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{orderReference}")
    @Operation(summary = "Get an order by its reference, e.g. ORD-AB12CD34")
    public ResponseEntity<Order> getOrder(@PathVariable String orderReference) {
        return ResponseEntity.ok(orderService.getOrderByReference(orderReference));
    }

    @GetMapping
    @Operation(summary = "List all orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
