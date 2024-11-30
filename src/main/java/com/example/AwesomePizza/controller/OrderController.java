package com.example.AwesomePizza.controller;

import com.example.AwesomePizza.entities.Order;
import com.example.AwesomePizza.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    public Order createOrder() {
        Order order = new Order();
        order.setStatus("NEW");
        int queueSize = orderRepository.findAll().size();
        order.setQueuePosition(queueSize + 1); // Positioning order at the last spot in the queue
        return orderRepository.save(order);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestParam String status) {
        Order order = orderRepository.findById(id).orElseThrow(RuntimeException::new);
        order.setStatus(status);
        if (status.equals("IN_PROGRESS")) {
            order.setQueuePosition(1); // An order set to "IN_PROGRESS" will always be at the first position in the queue
        } else if (status.equals("COMPLETED")) {
            order.setQueuePosition(null); // Remove queue position for completed orders
        }
        return orderRepository.save(order);
    }

    // New method to take a specific order
    @PutMapping("/{id}/take")
    public Order takeOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getStatus().equals("NEW")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is not in NEW status");
        }
        order.setStatus("IN_PROGRESS");
        order.setQueuePosition(1); // Set the order as the first in the queue
        return orderRepository.save(order);
    }

    @PutMapping("/takeNextOrder")
    public Order takeNextOrder() {
        List<Order> orders = orderRepository.findAllByStatusOrderByQueuePositionAsc("NEW");
        if (!orders.isEmpty()) {
            Order nextOrder = orders.get(0);
            nextOrder.setStatus("IN_PROGRESS");
            orderRepository.save(nextOrder);
            return nextOrder;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No NEW orders in queue!");
        }
    }

    @GetMapping("/queue")
    public List<Order> getOrdersQueue() {
        return orderRepository.findAllByStatusIn(Arrays.asList("NEW", "IN_PROGRESS"));
    }
}
