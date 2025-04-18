package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePage extends BaseActivity {
    private static final String TAG = "ProfilePage";
    private TextView nameTextView, emailTextView, phoneTextView;
    private Button logoutButton, deleteAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Инициализация UI
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        logoutButton = findViewById(R.id.logoutButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        // Загрузка данных пользователя
        loadUserData();

        // Обработчик выхода из аккаунта
        logoutButton.setOnClickListener(v -> {
            Log.d(TAG, "Выход из аккаунта");
            AuthUtils.clearUserData(this);
            Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Обработчик удаления аккаунта
        deleteAccountButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Удаление аккаунта")
                    .setMessage("Вы уверены, что хотите удалить аккаунт? Это действие нельзя отменить.")
                    .setPositiveButton("Удалить", (dialog, which) -> deleteAccount())
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    private void loadUserData() {
        int userId = AuthUtils.getUserId(this);
        if (userId == -1) {
            Log.w(TAG, "Пользователь не авторизован, перенаправление на MainActivity");
            Toast.makeText(this, "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Log.d(TAG, "Загрузка данных пользователя для userId: " + userId);
        Call<User> call = RetrofitClient.getApiService().getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    nameTextView.setText(user.getClientName() != null ? user.getClientName() : "Не указано");
                    emailTextView.setText(user.getEmail() != null ? user.getEmail() : "Не указано");
                    phoneTextView.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "Не указано");
                    Log.d(TAG, "Данные пользователя загружены: clientName=" + user.getClientName() +
                            ", email=" + user.getEmail() + ", phone=" + user.getPhoneNumber());
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                    Log.e(TAG, "Ошибка загрузки данных пользователя: " + response.code() + " - " + errorBody);
                    Toast.makeText(ProfilePage.this, "Ошибка загрузки данных: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке данных пользователя: " + t.getMessage());
                Toast.makeText(ProfilePage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAccount() {
        int userId = AuthUtils.getUserId(this);
        if (userId == -1) {
            Log.w(TAG, "Пользователь не авторизован");
            Toast.makeText(this, "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Отправка запроса на удаление аккаунта для userId: " + userId);
        Call<Void> call = RetrofitClient.getApiService().deleteUser(userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Аккаунт успешно удален");
                    AuthUtils.clearUserData(ProfilePage.this);
                    Toast.makeText(ProfilePage.this, "Аккаунт удален", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfilePage.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                    Log.e(TAG, "Ошибка удаления аккаунта: " + response.code() + " - " + errorBody);
                    Toast.makeText(ProfilePage.this, "Ошибка удаления аккаунта: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Ошибка сети при удалении аккаунта: " + t.getMessage());
                Toast.makeText(ProfilePage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getNavigationItemId() {
        return R.id.nav_profile;
    }
}