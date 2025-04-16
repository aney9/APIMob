package com.example.kurs;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Role {
    @SerializedName("idRole")
    private int idRole;

    @SerializedName("rolee1")
    private String rolee1;

    @SerializedName("userr") // Изменено с "userrs" на "userr"
    private List<User> userr; // Изменено с List<String> на List<User>

    public Role(int idRole, String rolee1, List<User> userr) {
        this.idRole = idRole;
        this.rolee1 = rolee1;
        this.userr = userr;
    }

    public int getIdRole() {
        return idRole;
    }

    public String getRolee1() {
        return rolee1;
    }

    public List<User> getUserr() {
        return userr;
    }
}