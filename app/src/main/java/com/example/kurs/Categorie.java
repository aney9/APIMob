package com.example.kurs;

import java.io.Serializable;
import java.util.List;

public class Categorie implements Serializable {
    private int idCategories;
    private String categories;
    private List<Product> catalogProducts;

    public Categorie(int idCategories, String catrgories, List<Product> catalogProducts)
    {
        this.idCategories = idCategories;
        this.categories = catrgories;
        this.catalogProducts = catalogProducts;
    }


    public int getIdCategories() {
        return idCategories;
    }

    public String getCategories() {
        return categories;
    }

    public List<Product> getCatalogProducts() {
        return catalogProducts;
    }
}
