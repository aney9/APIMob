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
    private Integer rolesId;  // Изменено с roles на rolesId для соответствия JSON
    private List<Object> reviews;  // Поле reviews, пока как List<Object>
    private Object roles;  // Добавлено поле roles как Object, так как может быть null в JSON

    // Конструктор
    public User(int idUsers, String loginvhod, String loginpassword, String phoneNumber,
                String clientName, String email, Integer rolesId, List<Object> reviews, Object roles) {
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

    public Integer getRolesId() { return rolesId; }
    public void setRolesId(Integer rolesId) { this.rolesId = rolesId; }

    public List<Object> getReviews() { return reviews; }
    public void setReviews(List<Object> reviews) { this.reviews = reviews; }

    public Object getRoles() { return roles; }
    public void setRoles(Object roles) { this.roles = roles; }
}