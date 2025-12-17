package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.UserDTO;
import com.example.backend.model.User;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints de registro y login")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.admin.code:123456789}")
    private String adminCreationCode;

    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema. Envía 'adminCode' para crear como ADMIN")
    @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "400", description = "Email y password requeridos")
    @ApiResponse(responseCode = "409", description = "Email ya registrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        // Validaciones simples
        if (req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email y password requeridos");
        }
        Optional<User> existing = userService.findByEmail(req.getEmail());
        if (existing.isPresent()) return ResponseEntity.status(409).body("Email ya registrado");
        // Soportar tanto 'nombre' como 'name' enviado por el frontend
        String requestedName = (req.getNombre() != null && !req.getNombre().isEmpty()) ? req.getNombre() : req.getName();

        // Determinar rol: por defecto USER; si se envía adminCode válido, crear ADMIN
        String role = "USER";
        String providedAdminCode = req.getAdminCode();
        if (providedAdminCode != null && providedAdminCode.equals(adminCreationCode)) {
            role = "ADMIN";
        }

        User u = User.builder()
                .name(requestedName)
                .email(req.getEmail())
                .password(req.getPassword())
                .role(role)
                .build();
        User created = userService.register(u);
        // generar token
        String token = jwtUtil.generateToken(created.getId(), created.getEmail(), created.getRole());
        UserDTO ud = new UserDTO();
        ud.setId(created.getId());
        ud.setEmail(created.getEmail());
        ud.setName(created.getName());
        ud.setRole(created.getRole());
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setMessage("Usuario registrado");
        resp.setUser(ud);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Login de usuario", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "400", description = "Email y password requeridos")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) return ResponseEntity.badRequest().body("Email y password requeridos");
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        UserDetails ud = (UserDetails) auth.getPrincipal();
        Optional<User> opt = userService.findByEmail(ud.getUsername());
        if (opt.isEmpty()) return ResponseEntity.status(401).body("Usuario no encontrado");
        User user = opt.get();
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setMessage("Login exitoso");
        resp.setUser(dto);
        return ResponseEntity.ok(resp);
    }
}
