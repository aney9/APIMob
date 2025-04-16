package com.example.kurs;

import java.io.Serializable;

public class Favorite implements Serializable {
    private int idFavorite;
    private String userId;
    private int catalogId;
    private String addedAt;
    private Product catalog;

    public Favorite(int idFavorite, String userId, int catalogId, String addedAt, Product catalog) {
        this.idFavorite = idFavorite;
        this.userId = userId;
        this.catalogId = catalogId;
        this.addedAt = addedAt;
        this.catalog = catalog;
    }

    public int getIdFavorite() { return idFavorite; }
    public String getUserId() { return userId; }
    public int getCatalogId() { return catalogId; }
    public String getAddedAt() { return addedAt; }
    public Product getCatalog() { return catalog; }
}