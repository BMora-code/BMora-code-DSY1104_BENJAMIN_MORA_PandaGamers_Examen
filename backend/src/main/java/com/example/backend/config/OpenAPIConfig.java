package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
// security scheme centralized in SwaggerConfig
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Nota: La configuración del esquema de seguridad (bearerAuth) se centraliza
        // en la clase SwaggerConfig para evitar esquemas duplicados.

        return new OpenAPI()
                // Información general de la API
                .info(new Info()
                        .title("PandaGamers API")
                        .version("1.0.0")
                        .description("API REST completa para la plataforma de e-commerce PandaGamers. " +
                                "Permite gestionar productos, ofertas, órdenes, usuarios y más. " +
                                "Utiliza autenticación JWT para endpoints protegidos.")
                        .contact(new Contact()
                                .name("PandaGamers Support")
                                .email("support@pandagamers.com")
                                .url("https://pandagamers.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                
                // Servidores disponibles
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de Desarrollo Local"))
                .addServersItem(new Server()
                        .url("http://127.0.0.1:8080")
                        .description("Servidor de Desarrollo Local (127.0.0.1)"))
                
                ;
    }
}
