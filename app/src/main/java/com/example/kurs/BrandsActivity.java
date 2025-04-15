package com.example.kurs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Re
sponse;

public class BrandsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private BrandsAdapter adapter;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private AmazonS3Client s3Client;
    private static final String BUCKET_NAME = "your-bucket-name"; // Замените на имя вашего бакета
    private static final String S3_ENDPOINT = "https://storage.yandexcloud.net";
    private static final String ACCESS_KEY = "your-access-key"; // Замените на ваш Access Key
    private static final String SECRET_KEY = "your-secret-key"; // Замените на ваш Secret Key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBaseLayout(R.layout.content_brands);

        // Инициализация S3 клиента
        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        s3Client = new AmazonS3Client(credentials);
        s3Client.setEndpoint(S3_ENDPOINT);

        recyclerView = findViewById(R.id.brandsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BrandsAdapter(brand -> {
            Intent intent = new Intent(BrandsActivity.this, DetailActivity.class);
            intent.putExtra("ITEM", brand);
            intent.putExtra("TYPE", "brand");
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        ImageButton addBrandButton = findViewById(R.id.addBrandButton);
        addBrandButton.setOnClickListener(v -> showAddBrandDialog());

        // Инициализация лаунчера для выбора изображения
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                Toast.makeText(this, "Изображение выбрано", Toast.LENGTH_SHORT).show();
            }
        });

        loadBrands();
    }

    private void showAddBrandDialog() {
        EditText input = new EditText(this);
        input.setHint("Название бренда");
        input.setPadding(32, 16, 32, 16);

        new AlertDialog.Builder(this)
                .setTitle("Добавить бренд")
                .setView(input)
                .setNeutralButton("Выбрать изображение", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    pickImageLauncher.launch(intent);
                })
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String brandName = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(brandName) && selectedImageUri != null) {
                        uploadImageToYandexCloud(selectedImageUri, imageUrl -> {
                            if (imageUrl != null) {
                                addBrand(brandName, imageUrl);
                            } else {
                                Toast.makeText(BrandsActivity.this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(BrandsActivity.this, "Введите название и выберите изображение", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    selectedImageUri = null;
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    private void uploadImageToYandexCloud(Uri imageUri, ImageUploadCallback callback) {
        try {
            String fileName = "brands/" + UUID.randomUUID().toString() + ".jpg";
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, fileName, inputStream, metadata);
            new Thread(() -> {
                try {
                    s3Client.putObject(request);
                    String imageUrl = s3Client.getUrl(BUCKET_NAME, fileName).toString();
                    runOnUiThread(() -> callback.onUploadComplete(imageUrl));
                } catch (Exception e) {
                    runOnUiThread(() -> callback.onUploadComplete(null));
                }
            }).start();
        } catch (Exception e) {
            callback.onUploadComplete(null);
        }
    }

    private interface ImageUploadCallback {
        void onUploadComplete(String imageUrl);
    }

    private void addBrand(String brandName, String imgUrl) {
        Brand newBrand = new Brand(0, brandName, imgUrl, new ArrayList<>());
        RetrofitClient.getApiService().createBrand(newBrand).enqueue(new Callback<Brand>() {
            @Override
            public void onResponse(Call<Brand> call, Response<Brand> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BrandsActivity.this, "Бренд добавлен", Toast.LENGTH_SHORT).show();
                    selectedImageUri = null;
                    loadBrands();
                } else {
                    Toast.makeText(BrandsActivity.this, "Ошибка добавления: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {
                Toast.makeText(BrandsActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBrands() {
        RetrofitClient.getApiService().getAllBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful()) {
                    adapter.updateBrands(response.body());
                } else {
                    Toast.makeText(BrandsActivity.this, "Ошибка загрузки брендов: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                Toast.makeText(BrandsActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}