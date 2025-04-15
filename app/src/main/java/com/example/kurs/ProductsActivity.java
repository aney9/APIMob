package com.example.kurs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private List<Categorie> categories;
    private List<Brand> brands;
    private AmazonS3Client s3Client;
    private static final String BUCKET_NAME = "your-bucket-name"; // Замените на имя вашего бакета
    private static final String S3_ENDPOINT = "https://storage.yandexcloud.net";
    private static final String ACCESS_KEY = "your-access-key"; // Замените на ваш Access Key
    private static final String SECRET_KEY = "your-secret-key"; // Замените на ваш Secret Key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBaseLayout(R.layout.activity_products);

        // Инициализация S3 клиента
        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        s3Client = new AmazonS3Client(credentials);
        s3Client.setEndpoint(S3_ENDPOINT);

        recyclerView = findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductsAdapter(product -> {
            Intent intent = new Intent(ProductsActivity.this, DetailActivity.class);
            intent.putExtra("ITEM", product);
            intent.putExtra("TYPE", "product");
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        ImageButton addProductButton = findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(v -> loadCategoriesAndBrandsForDialog());

        // Инициализация лаунчера для выбора изображения
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                Toast.makeText(this, "Изображение выбрано", Toast.LENGTH_SHORT).show();
            }
        });

        loadProducts();
    }

    private void loadCategoriesAndBrandsForDialog() {
        // Загружаем категории
        RetrofitClient.getApiService().getAllCategories().enqueue(new Callback<List<Categorie>>() {
            @Override
            public void onResponse(Call<List<Categorie>> call, Response<List<Categorie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    // Загружаем бренды после успешной загрузки категорий
                    loadBrandsForDialog();
                } else {
                    Toast.makeText(ProductsActivity.this, "Ошибка загрузки категорий", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Categorie>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBrandsForDialog() {
        RetrofitClient.getApiService().getAllBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brands = response.body();
                    showAddProductDialog();
                } else {
                    Toast.makeText(ProductsActivity.this, "Ошибка загрузки брендов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddProductDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Название продукта");
        layout.addView(nameInput);

        EditText priceInput = new EditText(this);
        priceInput.setHint("Цена");
        priceInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(priceInput);

        EditText descriptionInput = new EditText(this);
        descriptionInput.setHint("Описание");
        layout.addView(descriptionInput);

        EditText quantityInput = new EditText(this);
        quantityInput.setHint("Количество");
        quantityInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(quantityInput);

        Spinner categorySpinner = new Spinner(this);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                categories.stream().map(Categorie::getCategories).toArray(String[]::new));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        layout.addView(categorySpinner);

        Spinner brandSpinner = new Spinner(this);
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                brands.stream().map(Brand::getBrand1).toArray(String[]::new));
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);
        layout.addView(brandSpinner);

        new AlertDialog.Builder(this)
                .setTitle("Добавить продукт")
                .setView(layout)
                .setNeutralButton("Выбрать изображение", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    pickImageLauncher.launch(intent);
                })
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String priceStr = priceInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();
                    String quantityStr = quantityInput.getText().toString().trim();
                    int categoryIndex = categorySpinner.getSelectedItemPosition();
                    int brandIndex = brandSpinner.getSelectedItemPosition();

                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(priceStr) && !TextUtils.isEmpty(description) &&
                            !TextUtils.isEmpty(quantityStr) && selectedImageUri != null) {
                        try {
                            double price = Double.parseDouble(priceStr);
                            int quantity = Integer.parseInt(quantityStr);
                            int categoryId = categories.get(categoryIndex).getIdCategories();
                            int brandId = brands.get(brandIndex).getIdBrands();
                            uploadImageToYandexCloud(selectedImageUri, imageUrl -> {
                                if (imageUrl != null) {
                                    addProduct(name, price, description, imageUrl, quantity, brandId, categoryId);
                                } else {
                                    Toast.makeText(ProductsActivity.this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (NumberFormatException e) {
                            Toast.makeText(ProductsActivity.this, "Некорректный формат цены или количества", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProductsActivity.this, "Заполните все поля и выберите изображение", Toast.LENGTH_SHORT).show();
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
            String fileName = "products/" + UUID.randomUUID().toString() + ".jpg";
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

    private void addProduct(String name, double price, String description, String imgUrl, int quantity, int brandId, int categoryId) {
        Product newProduct = new Product(0, name, price, description, imgUrl, quantity, brandId, categoryId,
                null, new ArrayList<>(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        RetrofitClient.getApiService().createProduct(newProduct).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductsActivity.this, "Продукт добавлен", Toast.LENGTH_SHORT).show();
                    selectedImageUri = null;
                    loadProducts();
                } else {
                    Toast.makeText(ProductsActivity.this, "Ошибка добавления: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        RetrofitClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    adapter.updateProducts(response.body());
                } else {
                    Toast.makeText(ProductsActivity.this, "Ошибка загрузки продуктов: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}