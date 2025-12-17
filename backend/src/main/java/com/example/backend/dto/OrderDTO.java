package com.example.backend.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class OrderDTO {
    private String id;
    private String userId;
    private List<Object> productos;
    private double total;
    private String metodoPago;
    private String estado;
    private Instant fecha;
}
