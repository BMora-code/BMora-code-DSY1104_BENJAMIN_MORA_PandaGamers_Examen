package com.example.backend.dto;

public class OrderItemResponse {
    private String productId;
    private String productName;
    private double price;
    private int cantidad;
    private double subtotal;
    private String imageUrl;

    public OrderItemResponse() {
    }

    public OrderItemResponse(String productId, String productName, double price, int cantidad, double subtotal, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.imageUrl = imageUrl;
    }

    // Getters y Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
