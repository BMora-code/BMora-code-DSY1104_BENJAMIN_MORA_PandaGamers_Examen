package com.example.backend.controller;

import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.dto.ImportProductRequest;
import com.example.backend.dto.ProductResponse;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@Tag(name = "Productos", description = "Gestión de productos del catálogo")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private com.example.backend.service.ProductService productService;

    @Operation(summary = "Listar todos los productos", description = "Retorna la lista completa de productos disponibles en el catálogo")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> all() {
        List<Product> products = productRepository.findAll();
        // Si la colección está vacía, insertar los dos productos iniciales en la sección "Accesorios"
        if (products == null || products.isEmpty()) {
            Product p1 = Product.builder()
                .nombre("Auriculares HyperX")
                .descripcion("Auriculares gaming de alta calidad con sonido inmersivo.")
                .precio(79990)
                .categoria("Accesorios")
                .imagen("/images/Accesorios/Auriculares HyperX.webp")
                .stock(15)
                .createdAt(Instant.now())
                .build();

            Product p2 = Product.builder()
                .nombre("Control Inalámbrico")
                .descripcion("Control inalámbrico para consolas, cómodo y preciso.")
                .precio(59990)
                .categoria("Accesorios")
                .imagen("/images/Accesorios/Control inalámbrico.jpg")
                .stock(20)
                .createdAt(Instant.now())
                .build();

            productRepository.save(p1);
            productRepository.save(p2);
            products = productRepository.findAll();
        }
        // Si hay documentos con nombre vacío o nulo, rellenarlos con los dos productos esperados
        List<Product> missing = products.stream()
            .filter(p -> p.getNombre() == null || p.getNombre().trim().isEmpty())
            .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            // Datos esperados para los dos productos faltantes
            String[] nombres = {"Auriculares HyperX", "Control Inalámbrico"};
            String[] descripciones = {
                "Auriculares gaming de alta calidad con sonido inmersivo.",
                "Control inalámbrico para consolas, cómodo y preciso."
            };
            double[] precios = {79990, 59990};
            String categoria = "Accesorios";
            String[] imagenes = {"/images/Accesorios/Auriculares HyperX.webp", "/images/Accesorios/Control inalámbrico.jpg"};

            for (int i = 0; i < Math.min(missing.size(), 2); i++) {
                Product m = missing.get(i);
                m.setNombre(nombres[i]);
                m.setDescripcion(descripciones[i]);
                m.setPrecio(precios[i]);
                m.setCategoria(categoria);
                m.setImagen(imagenes[i]);
                if (m.getCreatedAt() == null) m.setCreatedAt(Instant.now());
                productRepository.save(m);
            }

            // Refrescar la lista tras correcciones
            products = productRepository.findAll();
        }

        // Asegurar que los productos destacados aparezcan primero en la lista
        List<String> wantedFirst = List.of("PlayStation 5", "PC ASUS ROG Strix");
        
        // Actualizar imagen de PC ASUS ROG Strix a ROG_Strix_Helios.jpg si tiene la otra
        for (Product p : products) {
            if ("PC ASUS ROG Strix".equals(p.getNombre()) && 
                (p.getImagen() == null || !p.getImagen().contains("ROG_Strix_Helios"))) {
                p.setImagen("/images/Portada/ROG_Strix_Helios.jpg");
                productRepository.save(p);
            }
        }
        
        List<Product> ordered = products.stream()
            .sorted((a, b) -> {
                int ia = wantedFirst.indexOf(a.getNombre());
                int ib = wantedFirst.indexOf(b.getNombre());
                if (ia == -1 && ib == -1) return 0;
                if (ia == -1) return 1;
                if (ib == -1) return -1;
                return Integer.compare(ia, ib);
            })
            .collect(Collectors.toList());

        List<ProductResponse> responses = ordered.stream()
            .map(ProductResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna los detalles de un producto específico")
    @ApiResponse(responseCode = "200", description = "Producto encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        Optional<Product> p = productRepository.findById(id);
        return p.map(product -> ResponseEntity.ok(new ProductResponse(product)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el catálogo (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Producto creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        product.setCreatedAt(Instant.now());
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Product incoming) {
        Optional<Product> op = productRepository.findById(id);
        if (op.isEmpty()) return ResponseEntity.notFound().build();
        Product p = op.get();
        p.setNombre(incoming.getNombre());
        p.setDescripcion(incoming.getDescripcion());
        p.setPrecio(incoming.getPrecio());
        p.setStock(incoming.getStock());
        p.setImagen(incoming.getImagen());
        p.setCategoria(incoming.getCategoria());
        productRepository.save(p);
        return ResponseEntity.ok(p);
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Actualizar stock de un producto", description = "Actualiza el stock disponible de un producto (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/stock/{id}")
    public ResponseEntity<?> updateStock(@PathVariable String id, @RequestBody StockUpdateRequest body) {
        Product updated = productService.updateStock(id, body.getStock());
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new ProductResponse(updated));
    }

    // DTO simple para actualizar stock
    public static class StockUpdateRequest {
        private int stock;

        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }

    @Operation(summary = "Importar productos", description = "Importa un lote de productos al catálogo. Si ya existen, los actualiza (requiere rol ADMIN)")
    @ApiResponse(responseCode = "200", description = "Productos importados exitosamente", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "No hay productos para importar")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/import")
    public ResponseEntity<?> importProducts(@RequestBody ImportProductRequest request) {
        if (request.getProducts() == null || request.getProducts().isEmpty()) {
            return ResponseEntity.badRequest().body("No products to import");
        }

        List<Product> imported = request.getProducts().stream()
            .map(item -> {
                // Verificar si ya existe un producto con el mismo nombre
                Optional<Product> existing = productRepository.findByNombre(item.getName());
                if (existing.isPresent()) {
                    // Actualizar el existente
                    Product p = existing.get();
                    p.setNombre(item.getName());
                    p.setDescripcion(item.getDescription());
                    p.setPrecio(item.getPrice());
                    p.setCategoria(item.getCategory());
                    p.setImagen(item.getImage());
                    p.setStock(item.getStock());
                    return p;
                } else {
                    // Crear uno nuevo
                    return Product.builder()
                        .nombre(item.getName())
                        .descripcion(item.getDescription())
                        .precio(item.getPrice())
                        .categoria(item.getCategory())
                        .imagen(item.getImage())
                        .stock(item.getStock())
                        .createdAt(Instant.now())
                        .build();
                }
            })
            .collect(Collectors.toList());

        List<Product> saved = productRepository.saveAll(imported);
        return ResponseEntity.ok(new ImportProductResponse(saved.size(), saved));
    }

    // Inner class para la respuesta
    public static class ImportProductResponse {
        private int count;
        private List<Product> products;

        public ImportProductResponse(int count, List<Product> products) {
            this.count = count;
            this.products = products;
        }

        public int getCount() {
            return count;
        }

        public List<Product> getProducts() {
            return products;
        }
    }
}

