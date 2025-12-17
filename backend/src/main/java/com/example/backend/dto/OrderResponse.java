package com.example.backend.dto;

import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.model.ShippingInfo;
import java.time.Instant;
import java.util.List;

public class OrderResponse {
    private String id;
    private String userId;
    private String status;
    private Instant createdAt;
    private double subtotal;
    private double iva;
    private double shippingCost;
    private double total;
    private String deliveryOption;
    private ShippingInfo shippingInfo;
    private List<OrderItem> items;
    private double duocDiscount;
    private boolean duocApplied;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.subtotal = order.getSubtotal();
        this.iva = order.getIva();
        this.shippingCost = order.getShippingCost();
        this.total = order.getTotal();
        this.deliveryOption = order.getDeliveryOption();
        this.shippingInfo = order.getShippingInfo();
        this.items = order.getItems();
        this.duocDiscount = order.getDuocDiscount();
        this.duocApplied = order.isDuocApplied();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(String deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getDuocDiscount() {
        return duocDiscount;
    }

    public void setDuocDiscount(double duocDiscount) {
        this.duocDiscount = duocDiscount;
    }

    public boolean isDuocApplied() {
        return duocApplied;
    }

    public void setDuocApplied(boolean duocApplied) {
        this.duocApplied = duocApplied;
    }
}
