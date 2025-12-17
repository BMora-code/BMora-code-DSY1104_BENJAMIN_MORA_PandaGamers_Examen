package com.example.backend.dto;

import com.example.backend.model.OrderItem;
import com.example.backend.model.ShippingInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CreateOrderRequest {
    @JsonProperty("items")
    private List<OrderItem> items;
    
    @JsonProperty("subtotal")
    private double subtotal;
    
    @JsonProperty("shippingCost")
    private double shippingCost;
    
    @JsonProperty("iva")
    private double iva;
    
    @JsonProperty("total")
    private double total;
    
    @JsonProperty("shippingInfo")
    private ShippingInfo shippingInfo;
    
    @JsonProperty("deliveryOption")
    private String deliveryOption;
    
    // Constructor vacío (requerido para Jackson)
    public CreateOrderRequest() {
    }
    
    // Constructor completo
    public CreateOrderRequest(List<OrderItem> items, double subtotal, double shippingCost, 
                             double iva, double total, ShippingInfo shippingInfo, String deliveryOption) {
        this.items = items;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.iva = iva;
        this.total = total;
        this.shippingInfo = shippingInfo;
        this.deliveryOption = deliveryOption;
    }
    
    // Getters y Setters
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    public double getShippingCost() {
        return shippingCost;
    }
    
    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }
    
    public double getIva() {
        return iva;
    }
    
    public void setIva(double iva) {
        this.iva = iva;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }
    
    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }
    
    public String getDeliveryOption() {
        return deliveryOption;
    }
    
    public void setDeliveryOption(String deliveryOption) {
        this.deliveryOption = deliveryOption;
    }
    
    @Override
    public String toString() {
        return "CreateOrderRequest{" +
                "items=" + items +
                ", subtotal=" + subtotal +
                ", shippingCost=" + shippingCost +
                ", iva=" + iva +
                ", total=" + total +
                ", shippingInfo=" + shippingInfo +
                ", deliveryOption='" + deliveryOption + '\'' +
                '}';
    }
}
