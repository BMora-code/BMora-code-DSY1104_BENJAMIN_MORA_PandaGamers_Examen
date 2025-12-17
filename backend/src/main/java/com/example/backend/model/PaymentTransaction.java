package com.example.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions")
public class PaymentTransaction {
    @Id
    private String id;
    private String orderId;
    private String userId;
    private double amount;
    private String token;
    private String status; // CREATED, APPROVED, REJECTED
    private String authorizationCode;
    private Instant expiresAt;
    private String returnUrl;
    private String finalUrl;
    private Instant createdAt;
}
