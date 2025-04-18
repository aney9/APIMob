package com.example.kurs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogPage extends AppCompatActivity {
    private static final String TAG = "CatalogPage";
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private ProgressBar progressBar;
    private List<Product> products = new ArrayList<>();
    private Map<Integer, Brand> brandsMap = new HashMap<>();
    private Map<Integer, Categorie> categoriesMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_page);

        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, products, false);
        productsRecyclerView.setAdapter(productAdapter);

        loadBrandsAndCategories();
    }

    private void loadBrandsAndCategories() {
        Call<List<Brand>> brandsCall = RetrofitClient.getApiService().getAllBrands();
        brandsCall.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brandsMap.clear();
                    for (Brand brand : response.body()) {
                        brandsMap.put(brand.getIdBrands(), brand);
                        Log.d(TAG, "Загружен бренд: ID=" + brand.getIdBrands() + ", Название=" + brand.getBrand1());
                    }
                    if (brandsMap.isEmpty()) {
                        Log.w(TAG, "Список брендов пуст");
                        Toast.makeText(CatalogPage.this, "Бренды не найдены", Toast.LENGTH_SHORT).show();
                    }
                    loadCategories();
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                    Log.e(TAG, "Ошибка загрузки брендов: " + response.code() + " - " + errorBody);
                    Toast.makeText(CatalogPage.this, "Ошибка загрузки брендов: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке брендов: " + t.getMessage());
                Toast.makeText(CatalogPage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        Call<List<Categorie>> categoriesCall = RetrofitClient.getApiService().getAllCategories();
        categoriesCall.enqueue(new Callback<List<Categorie>>() {
            @Override
            public void onResponse(Call<List<Categorie>> call, Response<List<Categorie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesMap.clear();
                    for (Categorie category : response.body()) {
                        categoriesMap.put(category.getIdCategories(), category);
                        Log.d(TAG, "Загружена категория: ID=" + category.getIdCategories() + ", Название=" + category.getCategories());
                    }
                    if (categoriesMap.isEmpty()) {
                        Log.w(TAG, "Список категорий пуст");
                        Toast.makeText(CatalogPage.this, "Категории не найдены", Toast.LENGTH_SHORT).show();
                    }
                    loadProducts();
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                    Log.e(TAG, "Ошибка загрузки категорий: " + response.code() + " - " + errorBody);
                    Toast.makeText(CatalogPage.this, "Ошибка загрузки категорий: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Categorie>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке категорий: " + t.getMessage());
                Toast.makeText(CatalogPage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Product>> call = RetrofitClient.getApiService().getAllProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    List<Product> productList = response.body();
                    if (productList == null || productList.isEmpty()) {
                        handleEmptyProducts();
                        return;
                    }

                    processProductList(productList);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                handleNetworkError(t);
            }
        });
    }

    private void processProductList(List<Product> productList) {
        products.clear();
        List<Product> validProducts = new ArrayList<>();

        for (Product product : productList) {
            try {
                Product processedProduct = processProduct(product);
                if (processedProduct != null && processedProduct.getId() != 0) {
                    validProducts.add(processedProduct);
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка обработки товара: " + e.getMessage());
            }
        }

        if (validProducts.isEmpty()) {
            handleEmptyProducts();
        } else {
            products.addAll(validProducts);
            productAdapter.notifyDataSetChanged();
        }
    }

    private Product processProduct(Product product) {
        Log.d(TAG, "Товар: ID=" + product.getId() +
                ", Название=" + product.getProductName() +
                ", Описание=" + product.getDescription());

        // Устанавливаем бренд и категорию
        Brand brand = brandsMap.get(product.getBrandId());
        Categorie category = categoriesMap.get(product.getCategoryId());

        if (brand == null) {
            Log.w(TAG, "Бренд с ID=" + product.getBrandId() + " не найден");
        }
        if (category == null) {
            Log.w(TAG, "Категория с ID=" + product.getCategoryId() + " не найдена");
        }

        product.setBrand(brand);
        product.setCategory(category);

        Log.d(TAG, "После обработки: Бренд=" + (brand != null ? brand.getBrand1() : "null") +
                ", Категория=" + (category != null ? category.getCategories() : "null"));

        return product;
    }

    private void handleEmptyProducts() {
        Log.w(TAG, "Список товаров пуст");
        Toast.makeText(CatalogPage.this, "Товары не найдены", Toast.LENGTH_SHORT).show();
    }

    private void handleErrorResponse(Response<?> response) {
        String errorBody = "";
        try {
            errorBody = response.errorBody() != null ? response.errorBody().string() : "Нет тела ошибки";
        } catch (IOException e) {
            Log.e(TAG, "Ошибка чтения тела ошибки", e);
        }
        Log.e(TAG, "Ошибка загрузки: " + response.code() + " - " + errorBody);
        Toast.makeText(CatalogPage.this, "Ошибка загрузки: " + response.code(), Toast.LENGTH_SHORT).show();
    }

    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "Ошибка сети: " + t.getMessage());
        Toast.makeText(CatalogPage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void addToFavorites(int productId) {
        if (!AuthUtils.isLoggedIn(this)) {
            Toast.makeText(this, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = AuthUtils.getClientName(this);
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Не удалось определить пользователя", Toast.LENGTH_SHORT).show();
            return;
        }

        Favorite favorite = new Favorite(0, userId, productId, null);
        Call<Favorite> call = RetrofitClient.getApiService().addFavorite(new FavoriteWrapper(favorite));

        call.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CatalogPage.this, "Товар добавлен в избранное", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Неизвестная ошибка";
                        Toast.makeText(CatalogPage.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(CatalogPage.this, "Ошибка при обработке ошибки", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {
                Toast.makeText(CatalogPage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}