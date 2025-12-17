package com.example.backend.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private String productId;
    private int cantidad;
}
