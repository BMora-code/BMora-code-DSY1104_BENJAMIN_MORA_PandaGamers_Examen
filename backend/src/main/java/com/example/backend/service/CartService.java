package com.example.backend.service;

import com.example.backend.model.Cart;
import com.example.backend.model.CartItem;
import com.example.backend.model.Product;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getOrCreateCart(String userId) {
        Optional<Cart> oc = cartRepository.findByUserId(userId);
        if (oc.isPresent()) return oc.get();
        Cart c = Cart.builder().userId(userId).items(new ArrayList<>()).updatedAt(Instant.now()).build();
        return cartRepository.save(c);
    }

    public Cart addToCart(String userId, String productId, int cantidad) {
        Cart cart = getOrCreateCart(userId);
        Optional<Product> op = productRepository.findById(productId);
        if (op.isEmpty()) throw new IllegalArgumentException("Producto no existe");
        Product p = op.get();
        if (p.getStock() < cantidad) throw new IllegalArgumentException("Stock insuficiente");

        List<CartItem> items = cart.getItems();
        boolean found = false;
        for (CartItem it : items) {
            if (it.getProductId().equals(productId)) {
                it.setCantidad(it.getCantidad() + cantidad);
                found = true;
                break;
            }
        }
        if (!found) items.add(CartItem.builder().productId(productId).cantidad(cantidad).build());
        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    public Cart clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setItems(new ArrayList<>());
        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }
}
