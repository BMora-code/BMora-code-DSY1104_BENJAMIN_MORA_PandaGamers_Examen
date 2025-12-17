package com.example.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class ImportProductRequest {
    private List<ProductImportItem> products;

    @Data
    public static class ProductImportItem {
        private String id;
        private String name;
        private String description;
        private double price;
        private String category;
        private String image;
        private int stock;
    }
}
