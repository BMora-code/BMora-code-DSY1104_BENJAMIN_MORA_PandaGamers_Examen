package com.example.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {
    @Id
    private String id;
    // keep both English and Spanish fields because controllers/DTOs use both
    private String name;
    private String nombre;
    private String email;
    private String password;
    private String role; // e.g., ROLE_USER, ROLE_ADMIN
    private Instant fecha;
    private Instant createdAt;
}
