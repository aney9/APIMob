package com.example.kurs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentPage extends AppCompatActivity {
    private EditText cardNumberEditText, expiryDateEditText, cvcEditText;
    private TextView totalAmountTextView;
    private Button payButton;
    private ProgressBar progressBar;
    private List<Cart> cartItems;
    private BigDecimal totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        cvcEditText = findViewById(R.id.cvcEditText);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        payButton = findViewById(R.id.payButton);
        progressBar = findViewById(R.id.progressBar);

        // Получение данных из интента
        Intent intent = getIntent();
        cartItems = (List<Cart>) intent.getSerializableExtra("cartItems");
        totalAmount = new BigDecimal(intent.getDoubleExtra("totalAmount", 0.0));
        totalAmountTextView.setText(String.format("Итого: ₽%.2f", totalAmount));

        payButton.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        String cardNumber = cardNumberEditText.getText().toString().trim();
        String expiryDate = expiryDateEditText.getText().toString().trim();
        String cvc = cvcEditText.getText().toString().trim();

        // Базовая валидация
        if (cardNumber.length() < 16) {
            Toast.makeText(this, "Введите полный номер карты", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            Toast.makeText(this, "Введите дату в формате MM/YY", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cvc.length() != 3) {
            Toast.makeText(this, "CVC должен содержать 3 цифры", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        payButton.setEnabled(false);

        // Получение email из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("userEmail", null);
        if (userEmail == null || userEmail.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            payButton.setEnabled(true);
            Toast.makeText(this, "Ошибка: email не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создание заказа
        String userId = AuthUtils.getClientName(this);
        String orderDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
        Order order = new Order(0, userId, totalAmount.doubleValue(), orderDate, cardNumber, expiryDate, cvc);

        // Создание элементов заказа
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cart : cartItems) {
            orderItems.add(new OrderItem(0, 0, cart.getCatalogId(), cart.getQuantity(), cart.getPrice().doubleValue(), null, null));
        }

        // Формирование текста чека
        StringBuilder receipt = new StringBuilder();
        receipt.append("Чек покупки\n");
        receipt.append("Дата: ").append(orderDate).append("\n");
        receipt.append("Пользователь: ").append(userId).append("\n");
        receipt.append("Email: ").append(userEmail).append("\n\n");
        receipt.append("Товары:\n");
        for (Cart cart : cartItems) {
            receipt.append("- Продукт ID: ").append(cart.getCatalogId())
                    .append(", Количество: ").append(cart.getQuantity())
                    .append(", Цена: ₽").append(String.format("%.2f", cart.getPrice()))
                    .append("\n");
        }
        receipt.append("\nИтого: ₽").append(String.format("%.2f", totalAmount)).append("\n");

        // Отправка заказа в API
        Call<Order> orderCall = RetrofitClient.getApiService().createOrder(order);
        orderCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                progressBar.setVisibility(View.GONE);
                payButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Отправка чека на email
                    new Thread(() -> {
                        try {
                            sendEmail(userEmail, "Чек покупки", receipt.toString());
                            runOnUiThread(() -> {
                                Toast.makeText(PaymentPage.this, "Оплата успешно завершена, чек отправлен на " + userEmail, Toast.LENGTH_LONG).show();
                                finish();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(PaymentPage.this, "Оплата прошла, но ошибка отправки чека: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(PaymentPage.this, "Ошибка при обработке оплаты", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                payButton.setEnabled(true);
                Toast.makeText(PaymentPage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendEmail(String recipientEmail, String subject, String body) throws MessagingException {
        // Настройки SMTP сервера (для Gmail)
        String host = "smtp.gmail.com";
        String port = "587";
        final String username = "your-email@gmail.com"; // Замените на ваш email
        final String password = "your-app-password"; // Замените на пароль приложения

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Создание сессии
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Создание сообщения
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setText(body);

        // Отправка сообщения
        Transport.send(message);
    }
}