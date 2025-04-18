package com.example.kurs;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("idReview")
    private int idReview;

    @SerializedName("rating")
    private int rating;

    @SerializedName("reviewText")
    private String reviewText;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("catalogroductId")
    private int catalogroductId;

    @SerializedName("usersId")
    private int usersId;

    @SerializedName("catalogroduct")
    private String catalogroduct;

    @SerializedName("users")
    private User users;

    public Review(int idReview, int rating, String reviewText, String createdAt, int catalogroductId, int usersId, String catalogroduct, User users) {
        this.idReview = idReview;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
        this.catalogroductId = catalogroductId;
        this.usersId = usersId;
        this.catalogroduct = catalogroduct;
        this.users = users;
    }

    public int getIdReview() {
        return idReview;
    }

    public void setIdReview(int idReview) {
        this.idReview = idReview;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getCatalogroductId() {
        return catalogroductId;
    }

    public void setCatalogroductId(int catalogroductId) {
        this.catalogroductId = catalogroductId;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public String getCatalogroduct() {
        return catalogroduct;
    }

    public void setCatalogroduct(String catalogroduct) {
        this.catalogroduct = catalogroduct;
    }

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }
}