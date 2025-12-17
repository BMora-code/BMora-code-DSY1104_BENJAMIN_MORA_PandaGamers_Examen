package com.example.backend.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String token;
    private String url;
    private String orderId;
    private double amount;
    private String message;
}
