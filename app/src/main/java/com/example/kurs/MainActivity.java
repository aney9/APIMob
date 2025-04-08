package com.example.kurs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE_ID = "roleId";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Проверка сохраненных данных при запуске
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedUserId = prefs.getInt(KEY_USER_ID, -1);
        if (savedUserId != -1) {
            // Если данные есть, пропускаем авторизацию
            Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
            intent.putExtra("USER_ID", savedUserId);
            startActivity(intent);
            finish();
            return;
        }

        // Инициализация элементов интерфейса
        emailEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    authenticateUser(email, password);
                } else {
                    Toast.makeText(MainActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void authenticateUser(String email, String password) {
        Log.d(TAG, "Попытка аутентификации для email: " + email);

        Call<List<User>> call = RetrofitClient.getApiService().getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                Log.d(TAG, "Ответ от сервера: " + response.code() + " - " + response.message());
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        List<User> users = response.body();
                        Log.d(TAG, "Получено пользователей: " + (users != null ? users.size() : "null"));

                        User foundUser = null;
                        for (User user : users) {
                            if (user.getEmail() != null && user.getEmail().equals(email)) {
                                foundUser = user;
                                break;
                            }
                        }

                        if (foundUser != null) {
                            Log.d(TAG, "Найден пользователь: " + foundUser.getEmail());

                            if (foundUser.getLoginpassword().equals(password)) {
                                if (foundUser.getRolesId() != null && foundUser.getRolesId() == 2) {
                                    // Сохранение данных пользователя
                                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt(KEY_USER_ID, foundUser.getIdUsers());
                                    editor.putString(KEY_EMAIL, foundUser.getEmail());
                                    editor.putInt(KEY_ROLE_ID, foundUser.getRolesId());
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
                                    intent.putExtra("USER_ID", foundUser.getIdUsers());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "Доступ только для определенной роли",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Неверный пароль",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Пользователь с такой почтой не найден",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Пользователь с email " + email + " не найден");
                        }
                    } else {
                        String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                        Toast.makeText(MainActivity.this,
                                "Ошибка при получении данных: " + response.code() + " - " + response.message() + " (" + errorBody + ")",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Неуспешный ответ: " + response.code() + " - " + response.message() + " - " + errorBody);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Ошибка сети: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Ошибка сети: " + t.getMessage(), t);
                });
            }
        });
    }
}