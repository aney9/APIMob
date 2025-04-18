package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Product implements Serializable {
    @SerializedName("idCatalogproducts")
    private int id;

    @SerializedName("productName")
    private String productName;

    @SerializedName("priceOfProduct")
    private BigDecimal price;

    @SerializedName("descriptionProduct")
    private String description;

    @SerializedName("img")
    private String imageUrl;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("brandsId")
    private int brandId;

    @SerializedName("categoriesId")
    private int categoryId;

    @SerializedName("brands")
    private Brand brand;

    @SerializedName("categories")
    private Categorie category;

    @SerializedName("carts")
    private List<Cart> carts;

    @SerializedName("favorites")
    private List<Favorite> favorites;

    @SerializedName("orderItems")
    private List<OrderItem> orderItems;

    @SerializedName("reviews")
    private List<Review> reviews;

    public Product() {
    }

    public Product(int id, String productName, BigDecimal price, String description,
                   String imageUrl, int quantity, Brand brand, Categorie category) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.brand = brand;
        this.category = category;
        this.brandId = brand != null ? brand.getIdBrands() : 0;
        this.categoryId = category != null ? category.getIdCategories() : 0;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
        this.brandId = brand != null ? brand.getIdBrands() : 0;
    }

    public Categorie getCategory() {
        return category;
    }

    public void setCategory(Categorie category) {
        this.category = category;
        this.categoryId = category != null ? category.getIdCategories() : 0;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", quantity=" + quantity +
                ", brandId=" + brandId +
                ", categoryId=" + categoryId +
                '}';
    }
}