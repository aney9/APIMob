package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
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
    private List<String> reviews;

    @SerializedName("roles")
    private Role roles;

    public User(int idUsers, String loginvhod, String loginpassword, String phoneNumber, String clientName, String email, int rolesId) {
        this.idUsers = idUsers;
        this.loginvhod = loginvhod;
        this.loginpassword = loginpassword;
        this.phoneNumber = phoneNumber;
        this.clientName = clientName;
        this.email = email;
        this.rolesId = rolesId;
        this.reviews = new ArrayList<>();
        this.roles = null;
    }

    // Геттеры и сеттеры
    public int getIdUsers() { return idUsers; }
    public void setIdUsers(int idUsers) { this.idUsers = idUsers; }
    public String getLoginvhod() { return loginvhod; }
    public void setLoginvhod(String loginvhod) { this.loginvhod = loginvhod; }
    public String getLoginpassword() { return loginpassword; }
    public void setLoginpassword(String loginpassword) { this.loginpassword = loginpassword; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getRolesId() { return rolesId; }
    public void setRolesId(int rolesId) { this.rolesId = rolesId; }
    public List<String> getReviews() { return reviews; }
    public void setReviews(List<String> reviews) { this.reviews = reviews; }
    public Role getRoles() { return roles; }
    public void setRoles(Role roles) { this.roles = roles; }
}