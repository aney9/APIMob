package com.example.kurs;

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

public class CatalogPage extends BaseActivity {
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catalog_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация RecyclerView
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, new ArrayList<>(), false);
        productsRecyclerView.setAdapter(productAdapter);

        // Загрузка товаров
        loadProducts();
    }

    private void loadProducts() {
        Call<List<Product>> call = RetrofitClient.getApiService().getAllProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter = new ProductAdapter(CatalogPage.this, response.body(), false);
                    productsRecyclerView.setAdapter(productAdapter);
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                    showToast("Ошибка загрузки товаров: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
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
        return R.id.nav_catalog;
    }
}