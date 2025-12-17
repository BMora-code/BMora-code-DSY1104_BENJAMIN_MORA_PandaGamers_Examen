package com.example.backend.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private String productId;
    private int cantidad;
}
