package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private ImageButton userInfoButton;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка: ID пользователя не получен", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.productsRecyclerView);
        userInfoButton = findViewById(R.id.userInfoButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(ProductsActivity.this, ProductDetailActivity.class);
                intent.putExtra("PRODUCT", product);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        userInfoButton.setOnClickListener(v -> showUserInfo());
        loadProducts();
    }

    private void loadProducts() {
        RetrofitClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    adapter.updateProducts(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this,
                        "Ошибка: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserInfo() {
        RetrofitClient.getApiService().getUser(currentUserId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String userInfo = "Имя: " + user.getClientName() + "\n" +
                            "Логин: " + user.getLoginvhod() + "\n" +
                            "Email: " + user.getEmail() + "\n" +
                            "Телефон: " + user.getPhoneNumber();

                    new AlertDialog.Builder(ProductsActivity.this)
                            .setTitle("Информация о пользователе")
                            .setMessage(userInfo)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setCancelable(true)
                            .show();
                } else {
                    Toast.makeText(ProductsActivity.this,
                            "Не удалось загрузить данные пользователя",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProductsActivity.this,
                        "Ошибка загрузки: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}