package com.example.kurs;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritePage extends BaseActivity {
    private RecyclerView favoritesRecyclerView;
    private ProductAdapter productAdapter;
    private Toast toast;
    private static final String TAG = "FavoritePage";
    private Map<Integer, Brand> brandsMap = new HashMap<>();
    private Map<Integer, Categorie> categoriesMap = new HashMap<>();
    private List<Product> products;

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

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this, products, true);
        favoritesRecyclerView.setAdapter(productAdapter);

        if (!AuthUtils.isLoggedIn(this)) {
            showToast("Пожалуйста, войдите в систему");
            finish();
            return;
        }

        loadBrandsAndCategories();
    }

    private void loadBrandsAndCategories() {
        Call<List<Brand>> brandsCall = RetrofitClient.getApiService().getAllBrands();
        brandsCall.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    brandsMap.clear();
                    for (Brand brand : response.body()) {
                        brandsMap.put(brand.getIdBrands(), brand);
                    }
                    loadCategories();
                } else {
                    handleError("Ошибка загрузки брендов", response);
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                handleNetworkError("при загрузке брендов", t);
            }
        });
    }

    private void loadCategories() {
        Call<List<Categorie>> categoriesCall = RetrofitClient.getApiService().getAllCategories();
        categoriesCall.enqueue(new Callback<List<Categorie>>() {
            @Override
            public void onResponse(Call<List<Categorie>> call, Response<List<Categorie>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    categoriesMap.clear();
                    for (Categorie category : response.body()) {
                        categoriesMap.put(category.getIdCategories(), category);
                    }
                    loadFavorites();
                } else {
                    handleError("Ошибка загрузки категорий", response);
                }
            }

            @Override
            public void onFailure(Call<List<Categorie>> call, Throwable t) {
                handleNetworkError("при загрузке категорий", t);
            }
        });
    }

    private void loadFavorites() {
        String userId = AuthUtils.getClientName(this);
        if (userId == null || userId.isEmpty()) {
            showToast("Не удалось определить пользователя");
            return;
        }

        Call<List<Favorite>> call = RetrofitClient.getApiService().getAllFavorites();
        call.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Favorite> userFavorites = response.body().stream()
                            .filter(f -> userId.equals(f.getUserId()))
                            .collect(Collectors.toList());

                    if (userFavorites.isEmpty()) {
                        showEmptyState();
                        return;
                    }

                    loadFavoriteProducts(userFavorites);
                } else {
                    handleError("Ошибка загрузки избранного", response);
                }
            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                handleNetworkError("при загрузке избранного", t);
            }
        });
    }

    private void loadFavoriteProducts(List<Favorite> favorites) {
        products.clear(); // Очищаем список перед загрузкой

        // Счетчик для отслеживания завершенных запросов
        final int[] loadedCount = {0};
        final int totalToLoad = favorites.size();

        if (totalToLoad == 0) {
            showEmptyState();
            return;
        }

        for (Favorite favorite : favorites) {
            int productId = favorite.getCatalogId();

            Call<Product> call = RetrofitClient.getApiService().getProduct(productId);
            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    loadedCount[0]++;

                    if (response.isSuccessful() && response.body() != null) {
                        Product product = response.body();
                        product.setBrand(brandsMap.get(product.getBrandId()));
                        product.setCategory(categoriesMap.get(product.getCategoryId()));

                        products.add(product);
                    }

                    // Обновляем адаптер после загрузки всех товаров
                    if (loadedCount[0] == totalToLoad) {
                        if (products.isEmpty()) {
                            showEmptyState();
                        } else {
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    loadedCount[0]++;
                    Log.e(TAG, "Ошибка загрузки товара с ID: " + productId, t);

                    // Все равно проверяем, все ли загружено
                    if (loadedCount[0] == totalToLoad) {
                        if (products.isEmpty()) {
                            showEmptyState();
                        } else {
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    private void processProducts(List<Product> products) {
        this.products.clear();

        for (Product product : products) {
            product.setBrand(brandsMap.get(product.getBrandId()));
            product.setCategory(categoriesMap.get(product.getCategoryId()));

            if (product.getId() != 0) {
                this.products.add(product);
            }
        }

        if (this.products.isEmpty()) {
            showEmptyState();
        } else {
            productAdapter.notifyDataSetChanged();
        }
    }

    private void showEmptyState() {
        showToast("Нет избранных товаров");
        productAdapter.notifyDataSetChanged();
    }

    private void handleError(String message, Response<?> response) {
        String errorBody = "";
        try {
            errorBody = response.errorBody() != null ? response.errorBody().string() : "";
        } catch (IOException e) {
            Log.e(TAG, "Error reading error body", e);
        }
        Log.e(TAG, message + ": " + response.code() + " - " + errorBody);
        showToast(message + ": " + response.code());
    }

    private void handleNetworkError(String context, Throwable t) {
        Log.e(TAG, "Ошибка сети " + context + ": " + t.getMessage());
        showToast("Ошибка сети " + context);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getNavigationItemId() {
        return R.id.nav_favorites;
    }
}