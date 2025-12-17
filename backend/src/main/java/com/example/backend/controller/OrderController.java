package com.example.backend.controller;

import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.model.Product;
import com.example.backend.model.User;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
@Tag(name = "Órdenes", description = "Gestión de órdenes y compras")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Crear nueva orden", description = "Crea una nueva orden de compra con los items especificados")
    @ApiResponse(responseCode = "200", description = "Orden creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos, usuario no existe, o stock insuficiente")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createOrder(@PathVariable String userId, @RequestBody CreateOrderRequest request) {
        logger.info("Iniciando creación de orden para usuario: {}", userId);
        logger.info("Request recibido: {}", request);
        
        if (request.getItems() == null) {
            logger.error("Items es null");
        } else {
            logger.info("Cantidad de items recibidos: {}", request.getItems().size());
            for (int i = 0; i < request.getItems().size(); i++) {
                OrderItem it = request.getItems().get(i);
                logger.info("Item {}: productId={}, name={}, price={}, quantity={}", 
                    i, it.getProductId(), it.getName(), it.getPrice(), it.getQuantity());
            }
        }
        
        Optional<User> uo = userRepository.findById(userId);
        if (uo.isEmpty()) {
            logger.error("Usuario no existe: {}", userId);
            return ResponseEntity.badRequest().body("Usuario no existe");
        }
        User user = uo.get();

        List<OrderItem> storedItems = new ArrayList<>();
        
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            logger.info("Procesando {} items", request.getItems().size());
            for (OrderItem it : request.getItems()) {
                logger.info("Procesando item: productId={}, name={}, price={}, quantity={}", 
                    it.getProductId(), it.getName(), it.getPrice(), it.getQuantity());
                
                // Verificar si el item tiene datos válidos
                if (it.getProductId() == null || it.getProductId().isEmpty()) {
                    logger.error("Item sin productId válido");
                    continue;
                }
                
                Optional<Product> po = productRepository.findById(it.getProductId());
                if (po.isEmpty()) {
                    logger.error("Producto no existe: {}", it.getProductId());
                    return ResponseEntity.badRequest().body("Producto no existe: " + it.getProductId());
                }
                Product p = po.get();
                if (p.getStock() < it.getQuantity()) {
                    logger.error("Stock insuficiente para: {}", p.getNombre());
                    return ResponseEntity.badRequest().body("Stock insuficiente: " + p.getNombre());
                }
                p.setStock(p.getStock() - it.getQuantity());
                productRepository.save(p);
                
                // Crear nuevo OrderItem: preferir el precio enviado por el frontend (puede incluir oferta),
                // si no viene, usar el precio actual del producto en la DB.
                double itemPrice = (it.getPrice() > 0) ? it.getPrice() : p.getPrecio();
                double originalPrice = p.getPrecio();
                int discount = 0;
                
                // Calcular descuento si el precio de la oferta es menor que el precio original del producto
                if (itemPrice < originalPrice) {
                    discount = (int) Math.round(((originalPrice - itemPrice) / originalPrice) * 100);
                }
                
                OrderItem stored = new OrderItem(
                    p.getId(),
                    p.getNombre(),
                    itemPrice,
                    it.getQuantity(),
                    p.getImagen(),
                    originalPrice,
                    discount
                );
                logger.info("Item guardado: productId={}, name={}, price={}, quantity={}, image={}", 
                    stored.getProductId(), stored.getName(), stored.getPrice(), stored.getQuantity(), stored.getImage());
                storedItems.add(stored);
            }
        } else {
            logger.warn("No hay items en el request o es null");
        }

        // Calcular descuento DUOC (20%) si el usuario pertenece a Duoc
        boolean duocApplied = false;
        double duocDiscount = 0.0;
        try {
            String email = user.getEmail() == null ? "" : user.getEmail().toLowerCase();
            String nombre = user.getNombre() == null ? "" : user.getNombre().toLowerCase();
            if (email.contains("duoc") || nombre.contains("duoc")) {
                duocApplied = true;
            }
        } catch (Exception ex) {
            logger.warn("No se pudo determinar afiliación DUOC del usuario: {}", ex.getMessage());
        }

        if (duocApplied) {
            double base = (request.getSubtotal() <= 0 ? 0 : request.getSubtotal()) +
                    (request.getShippingCost() <= 0 ? 0 : request.getShippingCost()) +
                    (request.getIva() <= 0 ? 0 : request.getIva());
            duocDiscount = Math.round((base * 0.20) * 100.0) / 100.0; // redondear a 2 decimales
        }

        double finalTotal = request.getTotal();
        if (duocApplied && duocDiscount > 0) {
            finalTotal = finalTotal - duocDiscount;
        }

        Order order = Order.builder()
                .userId(userId)
                .items(storedItems)
                .subtotal(request.getSubtotal())
                .shippingCost(request.getShippingCost())
                .iva(request.getIva())
                .total(finalTotal)
                .duocDiscount(duocDiscount)
                .duocApplied(duocApplied)
                .status("Pendiente")
                .deliveryOption(request.getDeliveryOption())
                .shippingInfo(request.getShippingInfo())
                .createdAt(Instant.now())
                .build();
        
        logger.info("Orden construida con {} items, guardando en MongoDB...", storedItems.size());
        logger.info("Items antes de guardar: {}", storedItems);
        for (int i = 0; i < storedItems.size(); i++) {
            Object obj = storedItems.get(i);
            logger.info("Tipo de storedItems[{}] = {}", i, obj == null ? "null" : obj.getClass().getName());
        }
        
        Order saved = orderRepository.save(order);
        logger.info("Orden guardada exitosamente con ID: {}", saved.getId());
        logger.info("Items guardados en orden: {}", saved.getItems());
        if (saved.getItems() != null) {
            for (int i = 0; i < saved.getItems().size(); i++) {
                Object obj = saved.getItems().get(i);
                logger.info("Tipo de saved.getItems()[{}] = {}", i, obj == null ? "null" : obj.getClass().getName());
            }
        }
        if (saved.getItems() != null) {
            for (OrderItem item : saved.getItems()) {
                logger.info("Item guardado en MongoDB: productId={}, name={}, price={}, quantity={}", 
                    item.getProductId(), item.getName(), item.getPrice(), item.getQuantity());
            }
        }
        
        return ResponseEntity.ok(new OrderResponse(saved));
    }

    @Operation(summary = "Obtener órdenes del usuario", description = "Retorna el historial de órdenes de un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(new OrderResponse(order));
        }
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtener orden por ID", description = "Retorna los detalles de una orden específica")
    @ApiResponse(responseCode = "200", description = "Orden encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) {
        Optional<Order> o = orderRepository.findById(id);
        return o.map(order -> ResponseEntity.ok(new OrderResponse(order)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todas las órdenes", description = "Retorna el listado de todas las órdenes (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) responses.add(new OrderResponse(order));
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Eliminar orden", description = "Elimina una orden del sistema (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Orden eliminada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        orderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Completar orden", description = "Marca una orden como completada después del pago exitoso")
    @ApiResponse(responseCode = "200", description = "Orden completada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable String id) {
        Optional<Order> o = orderRepository.findById(id);
        if (o.isEmpty()) return ResponseEntity.notFound().build();
        
        Order order = o.get();
        order.setStatus("Completada");
        Order saved = orderRepository.save(order);
        return ResponseEntity.ok(new OrderResponse(saved));
    }

    @Operation(summary = "Actualizar estado de orden", description = "Actualiza el estado de una orden (p.ej., a 'Failed' si el pago falla)")
    @ApiResponse(responseCode = "200", description = "Estado actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        Optional<Order> o = orderRepository.findById(id);
        if (o.isEmpty()) return ResponseEntity.notFound().build();
        
        Order order = o.get();
        String newStatus = body.get("status");
        if (newStatus != null && !newStatus.isEmpty()) {
            order.setStatus(newStatus);
            logger.info("Actualizando estado de orden {} a {}", id, newStatus);
        }
        Order saved = orderRepository.save(order);
        return ResponseEntity.ok(new OrderResponse(saved));
    }
}
