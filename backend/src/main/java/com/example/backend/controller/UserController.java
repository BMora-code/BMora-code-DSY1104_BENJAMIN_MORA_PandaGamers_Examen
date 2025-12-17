package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Gestión de usuarios y perfiles")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // Registro y login movidos a AuthController

    @Operation(summary = "Obtener perfil de usuario", description = "Retorna el perfil del usuario autenticado actual")
    @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("No autenticado");
        String email = authentication.getName();
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) return ResponseEntity.status(404).body("Usuario no encontrado");
        User u = user.get();
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }

    @Operation(summary = "Listar todos los usuarios", description = "Retorna el listado de todos los usuarios registrados (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> allUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Crear usuario (Admin)", description = "Crea un nuevo usuario en el sistema (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "400", description = "Email y password requeridos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/create")
    public ResponseEntity<?> createUserByAdmin(@RequestBody User incoming) {
        if (incoming.getEmail() == null || incoming.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email y password requeridos");
        }
        // Si no se especifica rol, por defecto USER
        if (incoming.getRole() == null) incoming.setRole("USER");
        User created = userService.register(incoming);
        created.setPassword(null);
        return ResponseEntity.ok(created);
    }
}
