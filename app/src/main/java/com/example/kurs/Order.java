package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Order {
    @SerializedName("idOrder")
    private int idOrder;

    @SerializedName("userId")
    private String userId;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("orderDate")
    private String orderDate;

    @SerializedName("cardNumber")
    private String cardNumber;

    @SerializedName("expiryDate")
    private String expiryDate;

    @SerializedName("cvc")
    private String cvc;

    @SerializedName("orderItems")
    private List<String> orderItems;

    public Order(int idOrder, String userId, double totalAmount, String orderDate, String cardNumber, String expiryDate, String cvc) {
        this.idOrder = idOrder;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvc = cvc;
        this.orderItems = new ArrayList<>();
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public List<String> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<String> orderItems) {
        this.orderItems = orderItems;
    }
}