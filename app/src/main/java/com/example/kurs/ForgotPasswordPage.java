package com.example.kurs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Properties;
import java.util.Random;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPasswordPage extends AppCompatActivity {
    private EditText emailEditText;
    private String verificationCode;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Разрешить сетевые операции в главном потоке (для простоты, в продакшене используйте AsyncTask или Coroutines)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        emailEditText = findViewById(R.id.emailEditText);
        Button confirmEmailButton = findViewById(R.id.confirmEmailButton);

        confirmEmailButton.setOnClickListener(v -> {
            userEmail = emailEditText.getText().toString().trim();
            if (userEmail.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!userEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Генерация кода подтверждения
            verificationCode = generateVerificationCode();
            sendVerificationEmail(userEmail, verificationCode);
            showVerificationDialog();
        });
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    private void sendVerificationEmail(String toEmail, String code) {
        final String username = "tema.murashov.06@mail.ru"; // Замените на ваш email
        final String password = "ZEmr6VqhLLGqquyuUJvB"; // Замените на пароль приложения mail.ru

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.mail.ru");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Код подтверждения для сброса пароля");
            message.setText("Ваш код подтверждения: " + code);

            Transport.send(message);
            Toast.makeText(this, "Код отправлен на " + toEmail, Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(this, "Ошибка отправки email: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void showVerificationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_verify_code);
        dialog.setCancelable(false);

        EditText codeEditText = dialog.findViewById(R.id.codeEditText);
        Button verifyCodeButton = dialog.findViewById(R.id.verifyCodeButton);

        verifyCodeButton.setOnClickListener(v -> {
            String enteredCode = codeEditText.getText().toString().trim();
            if (enteredCode.equals(verificationCode)) {
                Toast.makeText(this, "Код подтвержден", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Intent intent = new Intent(ForgotPasswordPage.this, ChangePassword.class);
                intent.putExtra("EMAIL", userEmail);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}