package com.example.kurs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartPage extends AppCompatActivity {
    private static final String TAG = "CartPage";
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private ProgressBar progressBar;
    private TextView totalPriceTextView;
    private List<Cart> cartItems = new ArrayList<>();
    private Map<Integer, Product> productsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_page);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems, productsMap);
        cartRecyclerView.setAdapter(cartAdapter);

        loadCartItems();
    }

    private void loadCartItems() {
        progressBar.setVisibility(View.VISIBLE);
        String clientName = AuthUtils.getClientName(this);
        if (clientName == null || clientName.isEmpty()) {
            Toast.makeText(this, "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Загрузка товаров
        Call<List<Product>> productsCall = RetrofitClient.getApiService().getAllProducts();
        productsCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productsMap.clear();
                    for (Product product : response.body()) {
                        productsMap.put(product.getId(), product);
                    }
                    // Загрузка корзины
                    loadCartFromApi(clientName);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CartPage.this, "Ошибка загрузки товаров", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CartPage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartFromApi(String clientName) {
        Call<List<Cart>> cartCall = RetrofitClient.getApiService().getAllCarts();
        cartCall.enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    List<Cart> userCart = response.body().stream()
                            .filter(cart -> cart.getUserId().equals(clientName))
                            .collect(Collectors.toList());
                    cartItems.addAll(userCart);
                    updateTotalPrice();
                    cartAdapter.notifyDataSetChanged();
                    if (cartItems.isEmpty()) {
                        Toast.makeText(CartPage.this, "Корзина пуста", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CartPage.this, "Ошибка загрузки корзины", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CartPage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (Cart cart : cartItems) {
            Product product = productsMap.get(cart.getCatalogId());
            if (product != null) {
                BigDecimal unitPrice = product.getPrice();
                BigDecimal quantity = BigDecimal.valueOf(cart.getQuantity());
                total = total.add(unitPrice.multiply(quantity));
            }
        }
        totalPriceTextView.setText(String.format("Итого: ₽%.2f", total));
    }
}