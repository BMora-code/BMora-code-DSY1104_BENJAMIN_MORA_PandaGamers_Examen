package com.example.backend.controller;

import com.example.backend.model.Offer;
import com.example.backend.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/offers")
@Tag(name = "Ofertas", description = "Gestión de ofertas y promociones")
public class OfferController {

    @Autowired
    private OfferRepository offerRepository;

    @Operation(summary = "Crear nueva oferta", description = "Crea una nueva oferta o promoción sobre un producto (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Oferta creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Offer.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Offer offer) {
        offer.setCreatedAt(Instant.now());
        Offer saved = offerRepository.save(offer);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Actualizar oferta", description = "Actualiza los detalles de una oferta existente (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Oferta actualizada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Offer.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "404", description = "Oferta no encontrada")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Offer incoming) {
        Optional<Offer> op = offerRepository.findById(id);
        if (op.isEmpty()) return ResponseEntity.notFound().build();
        Offer o = op.get();
        o.setDiscount(incoming.getDiscount());
        o.setPrice(incoming.getPrice());
        o.setProductId(incoming.getProductId());
        o.setProductName(incoming.getProductName());
        o.setProductImage(incoming.getProductImage());
        offerRepository.save(o);
        return ResponseEntity.ok(o);
    }

    @Operation(summary = "Eliminar oferta", description = "Elimina una oferta del sistema (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Oferta eliminada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        offerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar todas las ofertas (Admin)", description = "Retorna el listado de todas las ofertas activas (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Lista de ofertas obtenida", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<Offer>> all() {
        return ResponseEntity.ok(offerRepository.findAll());
    }

    @Operation(summary = "Listar ofertas públicas", description = "Retorna el listado de todas las ofertas activas visibles al público")
    @ApiResponse(responseCode = "200", description = "Lista de ofertas obtenida", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/all/public")
    public ResponseEntity<List<Offer>> publicAll() {
        return ResponseEntity.ok(offerRepository.findAll());
    }
}
