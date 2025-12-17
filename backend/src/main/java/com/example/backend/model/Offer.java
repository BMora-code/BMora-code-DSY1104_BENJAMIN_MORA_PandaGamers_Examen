package com.example.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "offers")
public class Offer {
    @Id
    private String id;
    private String productId;
    private int discount; // porcentaje
    private double price; // precio con descuento calculado
    private String productName;
    private String productImage;
    private Instant createdAt;
}
