package com.example.kurs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> products;
    private HashMap<Integer, Integer> favoriteIds;
    private boolean isFavoritesMode;
    private static final String TAG = "ProductAdapter";
    private Map<Integer, Brand> brandsMap = new HashMap<>();
    private Map<Integer, Categorie> categoriesMap = new HashMap<>();
    private Map<Integer, Integer> quantities = new HashMap<>();

    public ProductAdapter(Context context, List<Product> products, boolean isFavoritesMode) {
        this.context = context;
        this.products = products;
        this.favoriteIds = new HashMap<>();
        this.isFavoritesMode = isFavoritesMode;
        for (Product product : products) {
            quantities.put(product.getId(), 1);
        }
        loadBrandsAndCategories();
        if (AuthUtils.isLoggedIn(context)) {
            loadFavorites();
        } else {
            Log.w(TAG, "Пользователь не авторизован, пропускаем загрузку избранного");
        }
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
                    Log.d(TAG, "Всего брендов загружено: " + brandsMap.size());
                    if (brandsMap.isEmpty()) {
                        Log.w(TAG, "Список брендов пуст!");
                        Toast.makeText(context, "Не удалось загрузить бренды", Toast.LENGTH_SHORT).show();
                    }
                    loadCategories();
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                    Log.e(TAG, "Ошибка загрузки брендов: " + response.code() + " - " + errorBody);
                    Toast.makeText(context, "Ошибка загрузки брендов: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке брендов: " + t.getMessage());
                Toast.makeText(context, "Ошибка сети при загрузке брендов: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Log.d(TAG, "Всего категорий загружено: " + categoriesMap.size());
                    if (categoriesMap.isEmpty()) {
                        Log.w(TAG, "Список категорий пуст!");
                        Toast.makeText(context, "Не удалось загрузить категории...", Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged();
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                    Log.e(TAG, "Ошибка загрузки категорий: " + response.code() + " - " + errorBody);
                    Toast.makeText(context, "Ошибка загрузки категорий: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Categorie>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке категорий: " + t.getMessage());
                Toast.makeText(context, "Ошибка сети при загрузке категорий: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFavorites() {
        String clientName = AuthUtils.getClientName(context);
        if (clientName == null || clientName.isEmpty()) {
            Log.w(TAG, "clientName пустой или null, пропускаем загрузку избранного");
            return;
        }
        Log.d(TAG, "Загрузка избранного для clientName: " + clientName);
        Call<List<Favorite>> call = RetrofitClient.getApiService().getAllFavorites();
        call.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Favorite> allFavorites = response.body();
                    for (Favorite favorite : allFavorites) {
                        Log.d(TAG, "Favorite: idFavorite=" + favorite.getIdFavorite() +
                                ", userId=" + favorite.getUserId() +
                                ", catalogId=" + favorite.getCatalogId() +
                                ", addedAt=" + favorite.getAddedAt());
                    }
                    List<Favorite> userFavorites = allFavorites.stream()
                            .filter(favorite -> favorite.getUserId() != null && favorite.getUserId().equals(clientName))
                            .collect(Collectors.toList());
                    favoriteIds.clear();
                    for (Favorite favorite : userFavorites) {
                        favoriteIds.put(favorite.getCatalogId(), favorite.getIdFavorite());
                        Log.d(TAG, "Избранное: ID=" + favorite.getCatalogId() + ", idFavorite=" + favorite.getIdFavorite());
                    }
                    Log.d(TAG, "Загружено избранных записей: " + favoriteIds.size());
                    if (userFavorites.isEmpty()) {
                        Log.w(TAG, "Нет избранных записей для пользователя: " + clientName);
                    }
                    notifyDataSetChanged();
                } else {
                    String errorBody;
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "Нет тела ошибки";
                    } catch (IOException e) {
                        Log.e(TAG, "Ошибка чтения тела ошибки: " + e.getMessage());
                        errorBody = "Ошибка чтения тела ошибки";
                    }
                    Log.e(TAG, "Ошибка загрузки избранного: " + response.code() + " - " + errorBody);
                    Toast.makeText(context, "Ошибка загрузки избранного: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке избранного: " + t.getMessage());
                Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productNameTextView.setText(product.getProductName());
        holder.productPriceTextView.setText(String.format("₽%.2f", product.getPrice()));
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.productImageView);

        // Initialize quantity
        int currentQuantity = quantities.getOrDefault(product.getId(), 1);
        holder.quantityTextView.setText(String.valueOf(currentQuantity));
        quantities.put(product.getId(), currentQuantity);

        // Quantity controls
        holder.increaseQuantityButton.setOnClickListener(v -> {
            int qty = quantities.getOrDefault(product.getId(), 1);
            qty++;
            quantities.put(product.getId(), qty);
            holder.quantityTextView.setText(String.valueOf(qty));
        });

        holder.decreaseQuantityButton.setOnClickListener(v -> {
            int qty = quantities.getOrDefault(product.getId(), 1);
            if (qty > 1) {
                qty--;
                quantities.put(product.getId(), qty);
                holder.quantityTextView.setText(String.valueOf(qty));
            }
        });

        // Navigate to ProductDetailActivity on item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });

        // Hide add to cart button in favorites mode
        holder.addToCartButton.setVisibility(isFavoritesMode ? View.GONE : View.VISIBLE);
        holder.quantityLayout.setVisibility(isFavoritesMode ? View.GONE : View.VISIBLE);

        // Set favorite icon state
        boolean isFavorite = favoriteIds.containsKey(product.getId());
        holder.favoriteImageView.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_vorder);
        holder.favoriteImageView.setColorFilter(ContextCompat.getColor(context, isFavorite ? R.color.red : R.color.blue));

        // Disable favorite button if product ID is invalid
        if (product.getId() == 0) {
            holder.favoriteImageView.setEnabled(false);
            holder.favoriteImageView.setColorFilter(ContextCompat.getColor(context, R.color.red));
            return;
        }

        // Favorite button click handler
        holder.favoriteImageView.setOnClickListener(v -> {
            if (!AuthUtils.isLoggedIn(context)) {
                Log.w(TAG, "Пользователь не авторизован, перенаправление на MainActivity");
                Toast.makeText(context, "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                return;
            }

            String clientName = AuthUtils.getClientName(context);
            Log.d(TAG, "Клик на сердечко, clientName: " + clientName + ", Товар: " + product.getProductName() +
                    ", CatalogId: " + product.getId());
            if (isFavorite) {
                // Remove from favorites
                int favoriteId = favoriteIds.get(product.getId());
                Call<Void> call = RetrofitClient.getApiService().deleteFavorite(favoriteId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            favoriteIds.remove(product.getId());
                            notifyItemChanged(position);
                            Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorBody;
                            try {
                                errorBody = response.errorBody() != null ? response.errorBody().string() : "Нет тела ошибки";
                            } catch (IOException e) {
                                errorBody = "Ошибка чтения тела ошибки";
                            }
                            Log.e(TAG, "Ошибка удаления из избранного: " + response.code() + " - " + errorBody);
                            Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Ошибка сети при удалении из избранного: " + t.getMessage());
                        Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Add to favorites
                if (clientName == null || clientName.isEmpty()) {
                    Log.e(TAG, "clientName пустой или null, невозможно добавить в избранное");
                    Toast.makeText(context, "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String addedAt = sdf.format(new Date());
                Log.d(TAG, "Добавление в избранное: catalogId=" + product.getId() + ", userId=" + clientName + ", addedAt=" + addedAt);

                Favorite favorite = new Favorite(0, clientName, product.getId(), addedAt);
                FavoriteWrapper favoriteWrapper = new FavoriteWrapper(favorite);

                Gson gson = new Gson();
                String requestBody = gson.toJson(favoriteWrapper);
                Log.d(TAG, "Отправляемый запрос POST /api/Favorites: " + requestBody);

                Call<Favorite> call = RetrofitClient.getApiService().addFavorite(favoriteWrapper);
                call.enqueue(new Callback<Favorite>() {
                    @Override
                    public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Favorite addedFavorite = response.body();
                            favoriteIds.put(product.getId(), addedFavorite.getIdFavorite());
                            notifyItemChanged(position);
                            Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Успешно добавлено в избранное: idFavorite=" + addedFavorite.getIdFavorite());
                        } else {
                            String errorBody;
                            try {
                                errorBody = response.errorBody() != null ? response.errorBody().string() : "Нет тела ошибки";
                            } catch (IOException e) {
                                errorBody = "Ошибка чтения тела ошибки";
                            }
                            Log.e(TAG, "Ошибка добавления в избранное: " + response.code() + " - " + errorBody);
                            Toast.makeText(context, "Ошибка добавления: " + errorBody, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Favorite> call, Throwable t) {
                        Log.e(TAG, "Ошибка сети при добавлении в избранное: " + t.getMessage());
                        Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Обработчик кнопки добавления в корзину
        holder.addToCartButton.setOnClickListener(v -> {
            if (!AuthUtils.isLoggedIn(context)) {
                Log.w(TAG, "Пользователь не авторизован, перенаправление на MainActivity");
                Toast.makeText(context, "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                return;
            }

            String clientName = AuthUtils.getClientName(context);
            if (clientName == null || clientName.trim().isEmpty()) {
                Log.e(TAG, "clientName пустой или null");
                Toast.makeText(context, "Ошибка: имя пользователя не найдено", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Добавление в корзину, clientName: " + clientName);
            int qty = quantities.getOrDefault(product.getId(), 1);

            // Создаём объект Cart с catalog = null
            Cart cart = new Cart(qty, product.getPrice(), clientName.trim(), product.getId(), null);

            // Логируем запрос для отладки
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Cart.class, new CartSerializer())
                    .serializeNulls()
                    .create();
            Log.d(TAG, "Отправляемый запрос POST /api/Carts: " + gson.toJson(cart));

            // Отправляем объект Cart напрямую
            Call<Cart> call = RetrofitClient.getApiService().addToCart(cart);
            call.enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(Call<Cart> call, Response<Cart> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(context, product.getProductName() + " добавлен в корзину", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Успешный ответ: " + gson.toJson(response.body()));
                    } else {
                        String errorBody;
                        try {
                            errorBody = response.errorBody() != null ? response.errorBody().string() : "Нет тела ошибки";
                        } catch (IOException e) {
                            errorBody = "Ошибка чтения тела ответа: " + e.getMessage();
                        }
                        Log.e(TAG, "Ошибка добавления в корзину: " + response.code() + " - " + errorBody);
                        Toast.makeText(context, "Ошибка добавления: " + errorBody, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Cart> call, Throwable t) {
                    Log.e(TAG, "Ошибка сети при добавлении в корзину: " + t.getMessage());
                    Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        ImageView favoriteImageView;
        Button addToCartButton;
        Button increaseQuantityButton;
        Button decreaseQuantityButton;
        TextView quantityTextView;
        View quantityLayout;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            favoriteImageView = itemView.findViewById(R.id.favoriteImageView);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            quantityLayout = itemView.findViewById(R.id.quantityLayout);
        }
    }
}