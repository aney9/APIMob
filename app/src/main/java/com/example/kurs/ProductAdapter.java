package com.example.kurs;

import android.content.Context;
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
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> products;
    private HashMap<Integer, Integer> favoriteIds; // Хранит idFavorite для каждого catalogId
    private boolean isFavoritesMode;

    public ProductAdapter(Context context, List<Product> products, boolean isFavoritesMode) {
        this.context = context;
        this.products = products;
        this.favoriteIds = new HashMap<>();
        this.isFavoritesMode = isFavoritesMode;
        loadFavorites();
    }

    private void loadFavorites() {
        String userId = AuthUtils.getUserId(context);
        if (userId == null) {
            return; // Не загружаем избранное, если пользователь не авторизован
        }
        Call<List<Favorite>> call = RetrofitClient.getApiService().getFavorites(userId);
        call.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteIds.clear();
                    for (Favorite favorite : response.body()) {
                        favoriteIds.put(favorite.getCatalogId(), favorite.getIdFavorite());
                    }
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                Toast.makeText(context, "Ошибка загрузки избранного: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                .load(product.getImg())
                .into(holder.productImageView);

        // Скрыть кнопку "В корзину" в режиме избранного
        holder.addToCartButton.setVisibility(isFavoritesMode ? View.GONE : View.VISIBLE);

        // Установка состояния сердечка
        boolean isFavorite = favoriteIds.containsKey(product.getId());
        holder.favoriteImageView.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_vorder);
        holder.favoriteImageView.setColorFilter(ContextCompat.getColor(context, isFavorite ? R.color.red : R.color.blue));

        // Обработчик клика на сердечко
        holder.favoriteImageView.setOnClickListener(v -> {
            String userId = AuthUtils.getUserId(context);
            if (userId == null) {
                Toast.makeText(context, "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isFavorite) {
                // Удаление из избранного
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
                            Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Добавление в избранное
                Favorite favorite = new Favorite(0, userId, product.getId(), null, null);
                Call<Favorite> call = RetrofitClient.getApiService().addFavorite(favorite);
                call.enqueue(new Callback<Favorite>() {
                    @Override
                    public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            favoriteIds.put(product.getId(), response.body().getIdFavorite());
                            notifyItemChanged(position);
                            Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Favorite> call, Throwable t) {
                        Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Обработчик клика на кнопку "В корзину"
        holder.addToCartButton.setOnClickListener(v -> {
            Toast.makeText(context, product.getProductName() + " добавлен в корзину", Toast.LENGTH_SHORT).show();
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

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            favoriteImageView = itemView.findViewById(R.id.favoriteImageView);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}