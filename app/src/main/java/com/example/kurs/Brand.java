package com.example.kurs;

import java.io.Serializable;
import java.util.List;

public class Brand implements Serializable {
    private int idBrands;
    private String brand1;
    private String imgBrand;
    private List<Product> catalogProducts;

    public Brand(int idBrands, String brand1, String imgBrand, List<Product> catalogProducts){
        this.idBrands = idBrands;
        this.brand1 = brand1;
        this.imgBrand = imgBrand;
        this.catalogProducts = catalogProducts;
    }

    public int getIdBrands() {
        return idBrands;
    }

    public String getBrand1() {
        return brand1;
    }

    public String getImgBrand() {
        return imgBrand;
    }

    public List<Product> getCatalogProducts() {
        return catalogProducts;
    }
}
