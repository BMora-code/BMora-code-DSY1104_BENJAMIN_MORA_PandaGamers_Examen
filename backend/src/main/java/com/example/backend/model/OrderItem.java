package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

public class OrderItem {
    @Field("productId")
    @JsonProperty("productId")
    private String productId;
    
    @Field("name")
    @JsonProperty("name")
    private String name;
    
    @Field("price")
    @JsonProperty("price")
    private double price;
    
    @Field("quantity")
    @JsonProperty("quantity")
    private int quantity;
    
    @Field("image")
    @JsonProperty("image")
    private String image;
    
    @Field("originalPrice")
    @JsonProperty("originalPrice")
    private double originalPrice;
    
    @Field("discount")
    @JsonProperty("discount")
    private int discount;
    
    // Constructor vacío (requerido para Jackson)
    public OrderItem() {
    }
    
    // Constructor completo
    public OrderItem(String productId, String name, double price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Constructor con imagen
    public OrderItem(String productId, String name, double price, int quantity, String image) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.originalPrice = price;
        this.discount = 0;
    }
    
    // Constructor con originalPrice y discount
    public OrderItem(String productId, String name, double price, int quantity, String image, double originalPrice, int discount) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.originalPrice = originalPrice;
        this.discount = discount;
    }
    
    // Getters y Setters
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    public double getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public int getDiscount() {
        return discount;
    }
    
    public void setDiscount(int discount) {
        this.discount = discount;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", image='" + image + '\'' +
                ", originalPrice=" + originalPrice +
                ", discount=" + discount +
                '}';
    }
}
