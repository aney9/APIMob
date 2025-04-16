package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class User {
    @SerializedName("idUsers")
    private int idUsers;

    @SerializedName("loginvhod")
    private String loginvhod;

    @SerializedName("loginpassword")
    private String loginpassword;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("clientName")
    private String clientName;

    @SerializedName("email")
    private String email;

    @SerializedName("rolesId")
    private int rolesId;

    @SerializedName("reviews")
    private List<Object> reviews;

    @SerializedName("roles")
    private Role roles;

    // Конструктор
    public User(int idUsers, String loginvhod, String loginpassword, String phoneNumber,
                String clientName, String email, int rolesId, List<Object> reviews, Role roles) {
        this.idUsers = idUsers;
        this.loginvhod = loginvhod;
        this.loginpassword = loginpassword;
        this.phoneNumber = phoneNumber;
        this.clientName = clientName;
        this.email = email;
        this.rolesId = rolesId;
        this.reviews = reviews;
        this.roles = roles;
    }

    // Геттеры
    public int getIdUsers() {
        return idUsers;
    }

    public String getLoginvhod() {
        return loginvhod;
    }

    public String getLoginpassword() {
        return loginpassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public String getEmail() {
        return email;
    }

    public int getRolesId() {
        return rolesId;
    }

    public List<Object> getReviews() {
        return reviews;
    }

    public Role getRoles() {
        return roles;
    }
}