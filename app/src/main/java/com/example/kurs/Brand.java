package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Brand implements Serializable {
    @SerializedName("idBrands")
    private int idBrands;

    @SerializedName("brand1")
    private String brand1;

    @SerializedName("imgBrand")
    private String imgBrand;

    @SerializedName("catalogProducts")
    private List<String> catalogProducts;

    public Brand(int idBrands, String brand1, String imgBrand) {
        this.idBrands = idBrands;
        this.brand1 = brand1;
        this.imgBrand = imgBrand;
        this.catalogProducts = new ArrayList<>();
    }

    public int getIdBrands() {
        return idBrands;
    }

    public void setIdBrands(int idBrands) {
        this.idBrands = idBrands;
    }

    public String getBrand1() {
        return brand1;
    }

    public void setBrand1(String brand1) {
        this.brand1 = brand1;
    }

    public String getImgBrand() {
        return imgBrand;
    }

    public void setImgBrand(String imgBrand) {
        this.imgBrand = imgBrand;
    }

    public List<String> getCatalogProducts() {
        return catalogProducts;
    }

    public void setCatalogProducts(List<String> catalogProducts) {
        this.catalogProducts = catalogProducts;
    }
}