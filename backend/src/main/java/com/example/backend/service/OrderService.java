package com.example.backend.service;

import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.model.Product;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order createOrder(String userId, List<OrderItem> items) {
        for (OrderItem it : items) {
            Optional<Product> p = productRepository.findById(it.getProductId());
            if (p.isEmpty()) throw new IllegalArgumentException("Producto no existe: " + it.getProductId());
            Product prod = p.get();
            if (prod.getStock() < it.getQuantity()) throw new IllegalArgumentException("Stock insuficiente para: " + prod.getNombre());
            prod.setStock(prod.getStock() - it.getQuantity());
            productRepository.save(prod);
        }
        Order order = Order.builder()
                .userId(userId)
                .items(items)
                .status("Pendiente")
                .createdAt(Instant.now())
                .build();
        return orderRepository.save(order);
    }

    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
