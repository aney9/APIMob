package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CategoriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBaseLayout(R.layout.activity_categories);

        recyclerView = findViewById(R.id.categoriesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CategoriesAdapter(category -> {
            Intent intent = new Intent(CategoriesActivity.this, DetailActivity.class);
            intent.putExtra("ITEM", category);
            intent.putExtra("TYPE", "category");
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        ImageButton addCategoryButton = findViewById(R.id.addCategoryButton);
        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());

        loadCategories();
    }

    private void showAddCategoryDialog() {
        EditText input = new EditText(this);
        input.setHint("Название категории");
        input.setPadding(32, 16, 32, 16);

        new AlertDialog.Builder(this)
                .setTitle("Добавить категорию")
                .setView(input)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String categoryName = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(categoryName)) {
                        addCategory(categoryName);
                    } else {
                        Toast.makeText(CategoriesActivity.this, "Введите название категории", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void addCategory(String categoryName) {
        Categorie newCategory = new Categorie(0, categoryName, new ArrayList<>());
        RetrofitClient.getApiService().createCategory(newCategory).enqueue(new Callback<Categorie>() {
            @Override
            public void onResponse(Call<Categorie> call, Response<Categorie> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CategoriesActivity.this, "Категория добавлена", Toast.LENGTH_SHORT).show();
                    loadCategories(); // Обновляем список
                } else {
                    Toast.makeText(CategoriesActivity.this, "Ошибка добавления: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Categorie> call, Throwable t) {
                Toast.makeText(CategoriesActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getAllCategories().enqueue(new Callback<List<Categorie>>() {
            @Override
            public void onResponse(Call<List<Categorie>> call, Response<List<Categorie>> response) {
                if (response.isSuccessful()) {
                    adapter.updateCategories(response.body());
                } else {
                    Toast.makeText(CategoriesActivity.this, "Ошибка загрузки категорий: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Categorie>> call, Throwable t) {
                Toast.makeText(CategoriesActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}