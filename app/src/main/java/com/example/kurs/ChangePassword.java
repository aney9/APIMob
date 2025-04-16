package com.example.kurs;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ChangePassword extends AppCompatActivity {
    private EditText newPasswordEditText;
    private Button changePasswordButton;
    private String email;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = getIntent().getStringExtra("EMAIL");
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            if (newPassword.isEmpty()) {
                showToast("Пожалуйста, введите новый пароль");
                return;
            }
            updatePassword(email, newPassword);
        });
    }

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void updatePassword(String email, String newPassword) {
        Call<List<User>> call = RetrofitClient.getApiService().getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    User foundUser = null;
                    for (User user : users) {
                        if (user.getEmail() != null && user.getEmail().equals(email)) {
                            foundUser = user;
                            break;
                        }
                    }

                    if (foundUser != null) {
                        updateUserPassword(foundUser, newPassword);
                    } else {
                        runOnUiThread(() -> showToast("Пользователь с такой почтой не найден"));
                    }
                } else {
                    final String errorBody;
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "Нет тела ошибки";
                    } catch (Exception e) {
                        Log.e("ChangePassword", e.getMessage());
                        return;
                    }
                    runOnUiThread(() -> showToast("Ошибка: " + response.code() + " - " + errorBody));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> showToast("Ошибка сети: " + t.getMessage()));
            }
        });
    }

    private void updateUserPassword(User user, String newPassword) {
        // Создаем список userr для объекта Role
        List<User> userr = new ArrayList<>();
        // Создаем объект User для списка userr
        User roleUser = new User(
                user.getIdUsers(),
                user.getLoginvhod(),
                user.getLoginpassword(), // Используем текущий пароль
                user.getPhoneNumber(),
                user.getClientName(),
                user.getEmail(),
                2, // rolesId=2 для пользователя
                user.getReviews(),
                null // Устанавливаем roles как null, чтобы избежать рекурсии
        );
        userr.add(roleUser);

        // Создаем объект Role с ролью пользователя (idRole=2)
        Role role = new Role(2, "User", userr); // rolee1="User", предполагаем, что это правильное значение

        // Создаем обновленного пользователя с новым паролем и заполненным полем roles
        User updatedUser = new User(
                user.getIdUsers(),
                user.getLoginvhod(),
                newPassword,
                user.getPhoneNumber(),
                user.getClientName(),
                user.getEmail(),
                2, // rolesId=2 для пользователя
                user.getReviews(),
                role // Передаем объект Role
        );

        Call<User> updateCall = RetrofitClient.getApiService().updateUser(user.getIdUsers(), updatedUser);
        updateCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        showToast("Пароль успешно изменен для " + email);
                        finish();
                    });
                } else {
                    String errorBody = "Нет тела ошибки";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("ChangePassword", "Ошибка чтения тела ошибки: " + e.getMessage());
                    }
                    String finalErrorBody = errorBody;
                    runOnUiThread(() -> showToast("Ошибка обновления пароля: " + response.code() + " - " + finalErrorBody));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> showToast("Ошибка сети: " + t.getMessage()));
            }
        });
    }
}