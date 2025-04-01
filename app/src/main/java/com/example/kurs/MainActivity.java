package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                authenticateUser(email, password);
            }
        });
    }

    private void authenticateUser(String email, String password) {
        new Thread(() -> {
            try {
                Call<UserResponse> call = RetrofitClient.getApiService().getUserByEmail(email);
                Response<UserResponse> response = call.execute();

                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null && !response.body().getUsers().isEmpty()) {
                        User user = response.body().getUsers().get(0);  // Берем первого пользователя
                        if (user.getLoginpassword().equals(password)) {  // Проверка пароля
                            if (user.getRoles() != null && user.getRoles() == 1) {  // Проверка на админа
                                Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
                                intent.putExtra("USER_ID", user.getIdUsers());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Доступ только для админа",
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
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Ошибка: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}