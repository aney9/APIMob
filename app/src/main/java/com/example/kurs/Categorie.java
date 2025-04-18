package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Categorie implements Serializable {
    @SerializedName("idCategories")
    private int idCategories;

    @SerializedName("categories")
    private String categories;

    @SerializedName("catalogProducts")
    private List<String> catalogProducts;

    public Categorie(int idCategories, String categories) {
        this.idCategories = idCategories;
        this.categories = categories;
        this.catalogProducts = new ArrayList<>();
    }

    public int getIdCategories() {
        return idCategories;
    }

    public void setIdCategories(int idCategories) {
        this.idCategories = idCategories;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public List<String> getCatalogProducts() {
        return catalogProducts;
    }

    public void setCatalogProducts(List<String> catalogProducts) {
        this.catalogProducts = catalogProducts;
    }
}