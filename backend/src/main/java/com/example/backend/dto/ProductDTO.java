package com.example.backend.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String imagen;
    private String categoria;
}
