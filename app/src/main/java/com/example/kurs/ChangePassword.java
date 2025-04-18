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
    private static final String TAG = "ChangePassword";

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
                        runOnUiThread(() -> showToast("Пользователь с почтой " + email + " не найден"));
                        Log.w(TAG, "Пользователь с почтой " + email + " не найден");
                    }
                } else {
                    String errorBody;
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "Нет тела ошибки";
                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка чтения тела ошибки: " + e.getMessage());
                        errorBody = "Ошибка чтения тела ошибки";
                    }
                    final String finalErrorBody = errorBody;
                    runOnUiThread(() -> showToast("Ошибка загрузки пользователей: " + response.code() + " - " + finalErrorBody));
                    Log.e(TAG, "Ошибка загрузки пользователей: " + response.code() + " - " + finalErrorBody);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> showToast("Ошибка сети: " + t.getMessage()));
                Log.e(TAG, "Ошибка сети при загрузке пользователей: " + t.getMessage());
            }
        });
    }

    private void updateUserPassword(User user, String newPassword) {
        // Создаём обновлённого пользователя с новым паролем
        User updatedUser = new User(
                user.getIdUsers(),
                user.getLoginvhod(),
                newPassword,
                user.getPhoneNumber(),
                user.getClientName(),
                user.getEmail(),
                user.getRolesId()
        );

        // Устанавливаем reviews через сеттер
        updatedUser.setReviews(user.getReviews());
        // Поле roles оставляем null, так как оно не обязательно для запроса
        updatedUser.setRoles(null);

        Call<User> updateCall = RetrofitClient.getApiService().updateUser(user.getIdUsers(), updatedUser);
        updateCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        showToast("Пароль успешно изменён для " + email);
                        finish();
                    });
                    Log.d(TAG, "Пароль успешно обновлён для пользователя с ID=" + user.getIdUsers());
                } else {
                    String errorBody;
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "Нет тела ошибки";
                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка чтения тела ошибки: " + e.getMessage());
                        errorBody = "Ошибка чтения тела ошибки";
                    }
                    final String finalErrorBody = errorBody;
                    runOnUiThread(() -> showToast("Ошибка обновления пароля: " + response.code() + " - " + finalErrorBody));
                    Log.e(TAG, "Ошибка обновления пароля: " + response.code() + " - " + finalErrorBody);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> showToast("Ошибка сети: " + t.getMessage()));
                Log.e(TAG, "Ошибка сети при обновлении пароля: " + t.getMessage());
            }
        });
    }
}