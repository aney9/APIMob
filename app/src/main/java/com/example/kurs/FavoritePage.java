package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritePage extends BaseActivity {
    private RecyclerView favoritesRecyclerView;
    private ProductAdapter productAdapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация RecyclerView
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, new ArrayList<>(), true);
        favoritesRecyclerView.setAdapter(productAdapter);

        // Загрузка избранного
        loadFavorites();
    }

    private void loadFavorites() {
        String userId = AuthUtils.getUserId(this);
        Call<List<Favorite>> call = RetrofitClient.getApiService().getFavorites(userId);
        call.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = new ArrayList<>();
                    for (Favorite favorite : response.body()) {
                        if (favorite.getCatalog() != null) {
                            products.add(favorite.getCatalog());
                        }
                    }
                    productAdapter = new ProductAdapter(FavoritePage.this, products, true);
                    favoritesRecyclerView.setAdapter(productAdapter);
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                    showToast("Ошибка загрузки избранного: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                showToast("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected int getNavigationItemId() {
        return R.id.nav_favorites;
    }
}