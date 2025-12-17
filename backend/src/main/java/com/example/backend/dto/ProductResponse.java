package com.example.backend.dto;

import com.example.backend.model.Product;

public class ProductResponse {
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;
    private String imagen;
    private int stock;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.nombre = product.getNombre();
        this.descripcion = product.getDescripcion();
        this.precio = product.getPrecio();
        this.categoria = product.getCategoria();
        this.imagen = product.getImagen();
        this.stock = product.getStock();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
