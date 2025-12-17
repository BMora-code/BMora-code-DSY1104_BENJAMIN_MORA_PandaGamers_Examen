package com.example.backend.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String message;
    private UserDTO user;
}
