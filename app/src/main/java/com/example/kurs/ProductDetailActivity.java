package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    private Product product;
    private TextView quantityTextView;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Get product from intent
        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "Ошибка загрузки товара", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        ImageView productImageView = findViewById(R.id.productDetailImageView);
        TextView productNameTextView = findViewById(R.id.productDetailNameTextView);
        TextView productPriceTextView = findViewById(R.id.productDetailPriceTextView);
        TextView productDescriptionTextView = findViewById(R.id.productDetailDescriptionTextView);
        TextView productBrandTextView = findViewById(R.id.productDetailBrandTextView);
        TextView productCategoryTextView = findViewById(R.id.productDetailCategoryTextView);
        Button addToCartButton = findViewById(R.id.addToCartButton);
        Button increaseQuantityButton = findViewById(R.id.increaseQuantityButton);
        Button decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);
        quantityTextView = findViewById(R.id.quantityTextView);

        // Set product details
        productNameTextView.setText(product.getProductName());
        productPriceTextView.setText(String.format("₽%.2f", product.getPrice()));
        productDescriptionTextView.setText(product.getDescription() != null ? product.getDescription() : "Нет описания");
        productBrandTextView.setText(product.getBrand() != null ? product.getBrand().getBrand1() : "Неизвестный бренд");
        productCategoryTextView.setText(product.getCategory() != null ? product.getCategory().getCategories() : "Без категории");
        Glide.with(this).load(product.getImageUrl()).into(productImageView);
        quantityTextView.setText(String.valueOf(quantity));

        // Quantity controls
        increaseQuantityButton.setOnClickListener(v -> {
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
        });

        decreaseQuantityButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });

        // В методе onCreate, в обработчике addToCartButton
        addToCartButton.setOnClickListener(v -> {
            if (!AuthUtils.isLoggedIn(this)) {
                Toast.makeText(this, "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                return;
            }

            String clientName = AuthUtils.getClientName(this);
            if (clientName == null || clientName.trim().isEmpty()) {
                Log.e(TAG, "clientName пустой или null");
                Toast.makeText(this, "Ошибка: имя пользователя не найдено", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Добавление в корзину, clientName: " + clientName);

            // Создаём минимальный объект Product для Catalog
            Product catalog = new Product();
            catalog.setId(product.getId());
            catalog.setProductName(product.getProductName());
            catalog.setPrice(product.getPrice());

            // Создаём объект Cart
            Cart cart = new Cart(quantity, product.getPrice(), clientName.trim(), product.getId(), catalog);

            // Упаковываем в CartWrapper
            CartWrapper cartWrapper = new CartWrapper(cart);

            // Логируем запрос
            Gson gson = new Gson();
            Log.d(TAG, "Отправляемый запрос POST /api/Carts: " + gson.toJson(cartWrapper));

            Call<Cart> call = RetrofitClient.getApiService().addToCart(cartWrapper);
            call.enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(Call<Cart> call, Response<Cart> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ProductDetailActivity.this, product.getProductName() + " добавлен в корзину", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Успешный ответ: " + gson.toJson(response.body()));
                    } else {
                        String errorBody;
                        try {
                            errorBody = response.errorBody() != null ? response.errorBody().string() : "Нет тела ошибки";
                        } catch (IOException e) {
                            errorBody = "Ошибка чтения тела ответа: " + e.getMessage();
                        }
                        Log.e(TAG, "Ошибка добавления в корзину: " + response.code() + " - " + errorBody);
                        Toast.makeText(ProductDetailActivity.this, "Ошибка добавления в корзину: " + errorBody, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Cart> call, Throwable t) {
                    Log.e(TAG, "Ошибка сети: " + t.getMessage());
                    Toast.makeText(ProductDetailActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}