package com.example.backend.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private String productId;
    private String nombre;
    private double precio;
    private int cantidad;
}
