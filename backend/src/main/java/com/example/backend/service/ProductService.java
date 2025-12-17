package com.example.backend.service;

import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    public Product create(Product p) {
        p.setCreatedAt(Instant.now());
        return productRepository.save(p);
    }

    public Product update(String id, Product incoming) {
        Optional<Product> op = productRepository.findById(id);
        if (op.isEmpty()) return null;
        Product p = op.get();
        p.setNombre(incoming.getNombre());
        p.setDescripcion(incoming.getDescripcion());
        p.setPrecio(incoming.getPrecio());
        p.setStock(incoming.getStock());
        p.setImagen(incoming.getImagen());
        p.setCategoria(incoming.getCategoria());
        return productRepository.save(p);
    }

    // Actualización parcial: solo cambiar el stock sin reemplazar otros campos
    public Product updateStock(String id, int newStock) {
        Optional<Product> op = productRepository.findById(id);
        if (op.isEmpty()) return null;
        Product p = op.get();
        p.setStock(newStock);
        return productRepository.save(p);
    }

    public void delete(String id) {
        productRepository.deleteById(id);
    }
}
