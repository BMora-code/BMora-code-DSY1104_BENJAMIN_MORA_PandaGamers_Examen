package com.example.backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    // El frontend a veces envía 'nombre' (español) o 'name' (inglés).
    private String name;
    private String nombre;
    private String email;
    private String password;

    // Campo opcional para solicitar creación de usuario admin.
    private String adminCode;
    // Nota: No confiar en un campo 'role' enviado por el cliente; el servidor decide el rol
}
