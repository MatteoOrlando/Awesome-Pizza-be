package com.example.AwesomePizza.repository;

import com.example.AwesomePizza.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * This interface extends JpaRepository, providing CRUD operations for the Order entity.
 * JpaRepository comes with many operations for persisting, retrieving, deleting, and updating entities.

 * param Order: The entity type that the repository manages.
 * param Long: The type of the id of the entity the repository manages.
 */

public interface OrderRepository extends JpaRepository<Order, Long> {
    List <Order> findAllByStatusOrderByQueuePositionAsc(String status);
    List <Order> findAllByStatusIn(List <String> status);
}