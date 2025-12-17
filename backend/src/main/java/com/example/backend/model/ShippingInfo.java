package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

public class ShippingInfo {
    @Field("name")
    @JsonProperty("name")
    private String name;
    
    @Field("address")
    @JsonProperty("address")
    private String address;
    
    @Field("city")
    @JsonProperty("city")
    private String city;
    
    @Field("region")
    @JsonProperty("region")
    private String region;
    
    @Field("postalCode")
    @JsonProperty("postalCode")
    private String postalCode;
    
    @Field("phone")
    @JsonProperty("phone")
    private String phone;
    
    // Constructor vacío (requerido para Jackson)
    public ShippingInfo() {
    }
    
    // Constructor completo
    public ShippingInfo(String name, String address, String city, String region, String postalCode, String phone) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.phone = phone;
    }
    
    // Getters y Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Override
    public String toString() {
        return "ShippingInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
