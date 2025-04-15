package com.example.kurs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private TextView detailName, detailDescription;
    private Button updateButton, deleteButton;
    private Object item;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailName = findViewById(R.id.detailName);
        detailDescription = findViewById(R.id.detailDescription);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        item = getIntent().getSerializableExtra("ITEM");
        type = getIntent().getStringExtra("TYPE");

        if ("category".equals(type)) {
            Categorie category = (Categorie) item;
            detailName.setText(category.getCategories());
            detailDescription.setText("ID: " + category.getIdCategories());
        } else if ("brand".equals(type)) {
            Brand brand = (Brand) item;
            detailName.setText(brand.getBrand1());
            detailDescription.setText("Изображение: " + brand.getImgBrand());
        } else if ("product".equals(type)) {
            Product product = (Product) item;
            detailName.setText(product.getProductName());
            detailDescription.setText(product.getDescription());
        }

        updateButton.setOnClickListener(v -> updateItem());
        deleteButton.setOnClickListener(v -> deleteItem());
    }

    private void updateItem() {
        Toast.makeText(this, "Функция обновления не реализована", Toast.LENGTH_SHORT).show();
    }

    private void deleteItem() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<Void> call = null;

        if ("category".equals(type)) {
            Categorie category = (Categorie) item;
            call = apiService.deleteCategory(category.getIdCategories());
        } else if ("brand".equals(type)) {
            Brand brand = (Brand) item;
            call = apiService.deleteBrand(brand.getIdBrands());
        } else if ("product".equals(type)) {
            Product product = (Product) item;
            call = apiService.deleteProduct(product.getId());
        }

        if (call != null) {
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        finish();
                    } else {
                        Toast.makeText(DetailActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}