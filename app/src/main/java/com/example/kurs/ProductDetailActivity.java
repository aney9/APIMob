package com.example.kurs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        product = (Product) getIntent().getSerializableExtra("PRODUCT");

        TextView name = findViewById(R.id.detailName);
        TextView price = findViewById(R.id.detailPrice);
        TextView description = findViewById(R.id.detailDescription);
        ImageView image = findViewById(R.id.detailImage);

        name.setText(product.getProductName());
        price.setText(product.getPrice() + " ₽");
        description.setText(product.getDescription());

        Glide.with(this)
                .load(product.getImg())
                .into(image);

        findViewById(R.id.updateButton).setOnClickListener(v -> updateProduct());
        findViewById(R.id.deleteButton).setOnClickListener(v -> deleteProduct());
    }

    private void updateProduct() {
        // Реализация обновления
    }

    private void deleteProduct() {
        RetrofitClient.getApiService().deleteProduct(product.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this,
                                "Ошибка: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}