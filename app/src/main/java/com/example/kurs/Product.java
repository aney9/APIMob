package com.example.kurs;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String productName;
    private double price;
    private String description;
    private String img;
    private int quantity;
    private int brandId;
    private int categoryId;

    public Product(int id, String productName, double price, String description,
                   String img, int quantity, int brandId, int categoryId) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.img = img;
        this.quantity = quantity;
        this.brandId = brandId;
        this.categoryId = categoryId;
    }

    public int getId() { return id; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImg() { return img; }
    public int getQuantity() { return quantity; }
    public int getBrandId() { return brandId; }
    public int getCategoryId() { return categoryId; }
}