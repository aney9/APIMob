package com.example.kurs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    protected int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка: ID пользователя не получен", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    protected void setupBaseLayout(int contentLayoutResId) {
        setContentView(R.layout.activity_base);
        ImageButton userInfoButton = findViewById(R.id.userInfoButton);
        if (userInfoButton != null) {
            userInfoButton.setOnClickListener(v -> showUserInfo());
        }

        // Подключаем содержимое дочерней активности
        getLayoutInflater().inflate(contentLayoutResId, findViewById(R.id.contentContainer), true);
    }

    private void showUserInfo() {
        RetrofitClient.getApiService().getUser(currentUserId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String userInfo = "Имя: " + user.getClientName() + "\n" +
                            "Логин: " + user.getLoginvhod() + "\n" +
                            "Email: " + user.getEmail() + "\n" +
                            "Телефон: " + user.getPhoneNumber();

                    new AlertDialog.Builder(BaseActivity.this)
                            .setTitle("Информация о пользователе")
                            .setMessage(userInfo)
                            .setPositiveButton("Выход", (dialog, which) -> logout())
                            .setNegativeButton("OK", (dialog, which) -> dialog.dismiss())
                            .setCancelable(true)
                            .show();
                } else {
                    Toast.makeText(BaseActivity.this, "Не удалось загрузить данные пользователя", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(BaseActivity.this, "Ошибка загрузки: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}