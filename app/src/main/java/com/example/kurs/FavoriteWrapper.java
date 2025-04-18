package com.example.kurs;

public class FavoriteWrapper {
    private Favorite favorite;

    public FavoriteWrapper(Favorite favorite) {
        this.favorite = favorite;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }
}