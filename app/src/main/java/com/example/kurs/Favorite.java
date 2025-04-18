package com.example.kurs;

import com.google.gson.annotations.SerializedName;

public class Favorite {
    @SerializedName("idFavorite")
    private int idFavorite;

    @SerializedName("userId")
    private String userId;

    @SerializedName("catalogId")
    private int catalogId;

    @SerializedName("addedAt")
    private String addedAt;

    // Конструктор
    public Favorite(int idFavorite, String userId, int catalogId, String addedAt) {
        this.idFavorite = idFavorite;
        this.userId = userId;
        this.catalogId = catalogId;
        this.addedAt = addedAt;
    }

    // Геттеры и сеттеры
    public int getIdFavorite() { return idFavorite; }
    public void setIdFavorite(int idFavorite) { this.idFavorite = idFavorite; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getCatalogId() { return catalogId; }
    public void setCatalogId(int catalogId) { this.catalogId = catalogId; }
    public String getAddedAt() { return addedAt; }
    public void setAddedAt(String addedAt) { this.addedAt = addedAt; }
}