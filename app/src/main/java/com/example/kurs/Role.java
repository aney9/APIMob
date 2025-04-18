package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Role {
    @SerializedName("idRole")
    private int idRole;

    @SerializedName("rolee1")
    private String rolee1;

    @SerializedName("userrs")
    private List<String> userrs;

    public Role(int idRole, String rolee1) {
        this.idRole = idRole;
        this.rolee1 = rolee1;
        this.userrs = new ArrayList<>();
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getRolee1() {
        return rolee1;
    }

    public void setRolee1(String rolee1) {
        this.rolee1 = rolee1;
    }

    public List<String> getUserrs() {
        return userrs;
    }

    public void setUserrs(List<String> userrs) {
        this.userrs = userrs;
    }
}