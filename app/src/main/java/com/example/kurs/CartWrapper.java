package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CartWrapper implements Serializable {
    @SerializedName("Cart")
    private Cart cart;

    public CartWrapper(Cart cart) {
        this.cart = cart;
    }

    public Cart getCart() { return cart; }
}