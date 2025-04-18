package com.example.kurs;

import com.google.gson.annotations.SerializedName;

public class OrderItem {
    @SerializedName("idOrderitem")
    private int idOrderitem;

    @SerializedName("orderId")
    private int orderId;

    @SerializedName("catalogProductId")
    private int catalogProductId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private double price;

    @SerializedName("catalogProduct")
    private String catalogProduct;

    @SerializedName("order")
    private Order order;

    public OrderItem(int idOrderitem, int orderId, int catalogProductId, int quantity, double price, String catalogProduct, Order order) {
        this.idOrderitem = idOrderitem;
        this.orderId = orderId;
        this.catalogProductId = catalogProductId;
        this.quantity = quantity;
        this.price = price;
        this.catalogProduct = catalogProduct;
        this.order = order;
    }

    public int getIdOrderitem() {
        return idOrderitem;
    }

    public void setIdOrderitem(int idOrderitem) {
        this.idOrderitem = idOrderitem;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCatalogProductId() {
        return catalogProductId;
    }

    public void setCatalogProductId(int catalogProductId) {
        this.catalogProductId = catalogProductId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCatalogProduct() {
        return catalogProduct;
    }

    public void setCatalogProduct(String catalogProduct) {
        this.catalogProduct = catalogProduct;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}