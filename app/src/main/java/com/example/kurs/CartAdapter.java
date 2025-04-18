package com.example.kurs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<Cart> cartItems;
    private Map<Integer, Product> productsMap;
    private static final String TAG = "CartAdapter";

    public CartAdapter(Context context, List<Cart> cartItems, Map<Integer, Product> productsMap) {
        this.context = context;
        this.cartItems = cartItems;
        this.productsMap = productsMap;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartItems.get(position);
        Product product = productsMap.get(cart.getCatalogId());
        if (product == null) {
            holder.productNameTextView.setText("Товар не найден");
            return;
        }

        holder.productNameTextView.setText(product.getProductName());
        holder.productPriceTextView.setText(String.format("₽%.2f", cart.getPrice()));
        holder.quantityTextView.setText(String.valueOf(cart.getQuantity()));
        Glide.with(context).load(product.getImageUrl()).into(holder.productImageView);

        // Quantity controls
        holder.increaseQuantityButton.setOnClickListener(v -> {
            int newQuantity = cart.getQuantity() + 1;
            updateCartQuantity(cart, newQuantity, position);
        });

        holder.decreaseQuantityButton.setOnClickListener(v -> {
            int newQuantity = cart.getQuantity() - 1;
            if (newQuantity >= 1) {
                updateCartQuantity(cart, newQuantity, position);
            }
        });

        // Delete button
        holder.deleteButton.setOnClickListener(v -> {
            Call<Void> call = RetrofitClient.getApiService().deleteCart(cart.getIdCart());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartItems.size());
                        ((CartPage) context).updateTotalPrice();
                        Toast.makeText(context, "Товар удален из корзины", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Неизвестная ошибка";
                            Log.e(TAG, "Ошибка удаления: " + response.code() + " - " + errorBody);
                            Toast.makeText(context, "Ошибка удаления: " + errorBody, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e(TAG, "Ошибка чтения тела ошибки", e);
                            Toast.makeText(context, "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Ошибка сети: " + t.getMessage());
                    Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateCartQuantity(Cart cart, int newQuantity, int position) {
        Product product = productsMap.get(cart.getCatalogId());
        if (product == null) {
            Toast.makeText(context, "Товар не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаём минимальный объект Product для Catalog
        Product catalog = new Product();
        catalog.setId(product.getId());
        catalog.setProductName(product.getProductName());
        catalog.setPrice(product.getPrice());
        catalog.setDescription(product.getDescription() != null ? product.getDescription() : "");
        catalog.setImageUrl(product.getImageUrl() != null ? product.getImageUrl() : "");
        catalog.setQuantity(newQuantity);
        catalog.setBrandId(product.getBrandId());
        catalog.setCategoryId(product.getCategoryId());

        // Создаём обновленный объект Cart
        Cart updatedCart = new Cart(
                cart.getIdCart(),
                newQuantity,
                cart.getPrice(),
                cart.getUserId(),
                cart.getCatalogId(),
                catalog
        );

        // Упаковываем в CartWrapper
        CartWrapper cartWrapper = new CartWrapper(updatedCart);

        // Логируем запрос
        Gson gson = new Gson();
        Log.d(TAG, "Отправляемый запрос PUT /api/Carts/" + cart.getIdCart() + ": " + gson.toJson(cartWrapper));

        Call<Cart> call = RetrofitClient.getApiService().updateCart(cart.getIdCart(), cartWrapper);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.set(position, response.body());
                    notifyItemChanged(position);
                    ((CartPage) context).updateTotalPrice();
                    Toast.makeText(context, "Количество обновлено", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Неизвестная ошибка";
                        Log.e(TAG, "Ошибка обновления количества: " + response.code() + " - " + errorBody);
                        Toast.makeText(context, "Ошибка обновления: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Ошибка чтения тела ошибки", e);
                        Toast.makeText(context, "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView quantityTextView;
        Button increaseQuantityButton;
        Button decreaseQuantityButton;
        Button deleteButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}