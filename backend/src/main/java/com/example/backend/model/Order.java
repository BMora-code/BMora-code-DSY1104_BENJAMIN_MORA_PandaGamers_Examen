package com.example.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private String status;
    private Instant createdAt;
    private double subtotal;
    private double iva;
    private double shippingCost;
    private double total;
    private String deliveryOption;
    private ShippingInfo shippingInfo;
    private List<OrderItem> items;
    private double duocDiscount;
    private boolean duocApplied;
}

