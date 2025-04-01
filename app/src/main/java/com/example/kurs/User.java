package com.example.kurs;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private int idUsers;
    private String loginvhod;
    private String loginpassword;
    private String phoneNumber;
    private String clientName;
    private String email;
    private Integer roles;  // Используем Integer, так как может быть null
    private List<Object> reviews;  // Поле reviews, пока как List<Object>

    // Конструктор
    public User(int idUsers, String loginvhod, String loginpassword, String phoneNumber,
                String clientName, String email, Integer roles, List<Object> reviews) {
        this.idUsers = idUsers;
        this.loginvhod = loginvhod;
        this.loginpassword = loginpassword;
        this.phoneNumber = phoneNumber;
        this.clientName = clientName;
        this.email = email;
        this.roles = roles;
        this.reviews = reviews;
    }

    // Геттеры
    public int getIdUsers() { return idUsers; }
    public String getLoginvhod() { return loginvhod; }
    public String getLoginpassword() { return loginpassword; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getClientName() { return clientName; }
    public String getEmail() { return email; }
    public Integer getRoles() { return roles; }
    public List<Object> getReviews() { return reviews; }
}