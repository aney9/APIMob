package com.example.kurs;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable {
    private int id;
    private String productName;
    private BigDecimal priceOfProduct;
    private String description;
    private String img;
    private int quantity;
    private int brandId;
    private int categoryId;

    public Product(int id, String productName, BigDecimal priceOfProduct, String description,
                   String img, int quantity, int brandId, int categoryId) {
        this.id = id;
        this.productName = productName;
        this.priceOfProduct = priceOfProduct;
        this.description = description;
        this.img = img;
        this.quantity = quantity;
        this.brandId = brandId;
        this.categoryId = categoryId;
    }

    public int getId() { return id; }
    public String getProductName() { return productName; }
    public BigDecimal getPrice() { return priceOfProduct; }
    public String getDescription() { return description; }
    public String getImg() { return img; }
    public int getQuantity() { return quantity; }
    public int getBrandId() { return brandId; }
    public int getCategoryId() { return categoryId; }
}