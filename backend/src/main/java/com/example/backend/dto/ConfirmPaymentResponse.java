package com.example.backend.dto;

import lombok.Data;

@Data
public class ConfirmPaymentResponse {
    private String status;
    private String authorizationCode;
    private String message;
    private String orderId;
}
