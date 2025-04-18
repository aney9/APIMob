package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigDecimal;

public class Cart implements Serializable {
    @SerializedName("idCart")
    private Integer idCart; // Изменяем на Integer, чтобы исключить из JSON при null

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("userId")
    private String UserId;

    @SerializedName("catalogId")
    private int catalogId;

    @SerializedName("catalog")
    private Product catalog;

    // Конструктор для создания новой записи (без idCart)
    public Cart(int quantity, BigDecimal price, String userId, int catalogId, Product catalog) {
        this.quantity = quantity;
        this.price = price;
        this.UserId = userId;
        this.catalogId = catalogId;
        this.catalog = catalog;
    }

    // Конструктор для ответа сервера или обновления
    public Cart(Integer idCart, int quantity, BigDecimal price, String userId, int catalogId, Product catalog) {
        this.idCart = idCart;
        this.quantity = quantity;
        this.price = price;
        this.UserId = userId;
        this.catalogId = catalogId;
        this.catalog = catalog;
    }

    public Integer getIdCart() { return idCart; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public String getUserId() { return UserId; }
    public int getCatalogId() { return catalogId; }
    public Product getCatalog() { return catalog; }
}