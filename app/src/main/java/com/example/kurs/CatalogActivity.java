package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CatalogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBaseLayout(R.layout.activity_catalog);

        Button categoriesButton = findViewById(R.id.categoriesButton);
        Button brandsButton = findViewById(R.id.brandsButton);
        Button productsButton = findViewById(R.id.productsButton);

        categoriesButton.setOnClickListener(v -> {
            Intent intent = new Intent(CatalogActivity.this, CategoriesActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        brandsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CatalogActivity.this, BrandsActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        productsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CatalogActivity.this, ProductsActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });
    }
}