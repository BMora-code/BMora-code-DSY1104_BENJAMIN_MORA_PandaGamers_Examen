package com.example.backend;

import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner initializeProducts(ProductRepository productRepository) {
        return args -> {
            // Si no hay productos, importar los 36 del dataStore
            if (productRepository.count() == 0) {
                System.out.println("📦 Importando 36 productos del dataStore...");
                List<Product> products = new ArrayList<>();
                products.add(new Product(null, "Auriculares HyperX", "Auriculares gaming de alta calidad con sonido inmersivo.", 79990, 15, "Accesorios", "/images/Accesorios/Auriculares HyperX.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "Control Inalámbrico", "Control inalámbrico para consolas, cómodo y preciso.", 59990, 20, "Accesorios", "/images/Accesorios/Control inalámbrico.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Mousepad RGB", "Mousepad con iluminación RGB para setups gaming.", 29990, 25, "Accesorios", "/images/Accesorios/Mousepad RGB.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "Teclado Razer", "Teclado mecánico RGB con switches ópticos.", 149990, 10, "Accesorios", "/images/Accesorios/Teclado Razer.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "Nintendo Switch", "Consola híbrida para gaming en casa o movilidad.", 349990, 8, "Consolas", "/images/Consolas/Nintendo Switch.png", Instant.now(), Instant.now()));
                products.add(new Product(null, "PlayStation 4 Pro", "Consola de última generación con 4K HDR.", 399990, 5, "Consolas", "/images/Consolas/PlayStation 4 Pro.avif", Instant.now(), Instant.now()));
                products.add(new Product(null, "PlayStation 5", "La consola más potente con ray tracing y SSD ultra rápido.", 599990, 3, "Consolas", "/images/Consolas/PlayStation 5.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "Xbox Series X", "Consola Xbox de nueva generación con 4K gaming.", 549990, 4, "Consolas", "/images/Consolas/Xbox Series X.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Carcassonne", "Juego de estrategia medieval para construir ciudades.", 49990, 12, "Juegos de mesa", "/images/Juegos de mesa/Carcassonne.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Catan", "Juego de colonización y comercio en una isla.", 59990, 10, "Juegos de mesa", "/images/Juegos de mesa/Catán.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "Monopoly", "Clásico juego de propiedades y negocios.", 39990, 15, "Juegos de mesa", "/images/Juegos de mesa/Monopoly.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Risk", "Juego de estrategia global de conquista territorial.", 54990, 8, "Juegos de mesa", "/images/Juegos de mesa/Risk.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "HyperX Pulsefire", "Mouse gaming ergonómico con sensor óptico preciso.", 69990, 18, "Mouses", "/images/Mouses/HyperX Pulsefire.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "Logitech G502", "Mouse gaming con 11 botones programables.", 89990, 14, "Mouses", "/images/Mouses/Logitech G502.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "Razer DeathAdder", "Mouse ergonómico con sensor óptico de 16,000 DPI.", 79990, 16, "Mouses", "/images/Mouses/Razer DeathAdder.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "SteelSeries Rival 3", "Mouse gaming ligero con iluminación RGB.", 64990, 20, "Mouses", "/images/Mouses/SteelSeries Rival 3 –.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "PC Alienware", "PC gaming de alto rendimiento con RTX 3080.", 1999990, 2, "Pc Gamers", "/images/Pc Gamers/PC Alienware.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "PC ASUS ROG Strix", "PC gaming con componentes premium y RGB.", 1799990, 3, "Pc Gamers", "/images/Pc Gamers/PC ASUS ROG Strix.png", Instant.now(), Instant.now()));
                products.add(new Product(null, "PC HP Omen", "PC gaming equilibrado para gaming competitivo.", 1499990, 4, "Pc Gamers", "/images/Pc Gamers/PC HP Omen.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "PC MSI Gaming", "PC gaming con enfriamiento avanzado.", 1699990, 3, "Pc Gamers", "/images/Pc Gamers/PC MSI Gaming.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Polera Gamer 1", "Polera cómoda para gamers con diseño único.", 29990, 30, "Poleras", "/images/Poleras/640 (1).webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "Polera Gamer 2", "Polera con estampado de juegos.", 34990, 25, "Poleras", "/images/Poleras/3396_1.png", Instant.now(), Instant.now()));
                products.add(new Product(null, "Polera God of War", "Polera inspirada en God of War.", 39990, 20, "Poleras", "/images/Poleras/PLR-GOW.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Polera Papa Gamer", "Polera divertida para papás gamers.", 32990, 22, "Poleras", "/images/Poleras/polera-papa-de-dia-gamer-de-noche.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Polerón Gamer 1", "Polerón abrigado para sesiones largas de gaming.", 59990, 15, "Polerones", "/images/Polerones/1132_9.png", Instant.now(), Instant.now()));
                products.add(new Product(null, "Polerón Gamer 2", "Polerón con capucha y diseño moderno.", 64990, 12, "Polerones", "/images/Polerones/9704_9.png", Instant.now(), Instant.now()));
                products.add(new Product(null, "Polerón Smash Bros Vintage", "Polerón inspirado en Super Smash Bros con colores vintage.", 69990, 10, "Polerones", "/images/Polerones/poleron-smash-bros-vintage-colors.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Polerón Smash Ultimate", "Polerón de Super Smash Bros Ultimate.", 74990, 8, "Polerones", "/images/Polerones/poleron-smash-ultimate-2.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "HyperX Fury S", "Portamouse gaming con diseño ergonómico.", 39990, 18, "Portamouse", "/images/Portamouse/HyperX Fury S.avif", Instant.now(), Instant.now()));
                products.add(new Product(null, "Logitech G640", "Portamouse de tela para precisión máxima.", 49990, 16, "Portamouse", "/images/Portamouse/Logitech G640.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Razer Goliathus", "Portamouse con superficie de control óptima.", 44990, 20, "Portamouse", "/images/Portamouse/Razer Goliathus.png", Instant.now(), Instant.now()));
                products.add(new Product(null, "SteelSeries QcK", "Portamouse profesional para esports.", 52990, 14, "Portamouse", "/images/Portamouse/SteelSeries QcK.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Silla Cougar", "Silla gaming ergonómica con soporte lumbar.", 299990, 5, "Sillas", "/images/Sillas/Silla Cougar.webp", Instant.now(), Instant.now()));
                products.add(new Product(null, "Silla DXRacer", "Silla premium para gaming con ajuste completo.", 399990, 4, "Sillas", "/images/Sillas/Silla DXRacer.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Silla GT Omega", "Silla gaming cómoda con diseño moderno.", 349990, 6, "Sillas", "/images/Sillas/Silla GT Omega.jpg", Instant.now(), Instant.now()));
                products.add(new Product(null, "Silla SecretLab", "Silla de alta gama con materiales premium.", 499990, 3, "Sillas", "/images/Sillas/Silla SecretLab.webp", Instant.now(), Instant.now()));
                productRepository.saveAll(products);
                System.out.println("✅ 36 productos importados correctamente!");
            } else {
                System.out.println("ℹ️ Ya existen " + productRepository.count() + " productos en la BD");
            }
        };
    }
}
