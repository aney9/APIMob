package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class MainPage extends BaseActivity {
    private RecyclerView brandsRecyclerView;
    private RecyclerView faqRecyclerView;
    private BrandAdapter brandAdapter;
    private FAQAdapter faqAdapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Инициализация компонентов
        brandsRecyclerView = findViewById(R.id.brandsRecyclerView);
        faqRecyclerView = findViewById(R.id.faqRecyclerView);

        // Настройка RecyclerView для брендов (горизонтальная прокрутка)
        LinearLayoutManager brandsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        brandsRecyclerView.setLayoutManager(brandsLayoutManager);
        brandAdapter = new BrandAdapter(this, new ArrayList<>());
        brandsRecyclerView.setAdapter(brandAdapter);

        // Настройка RecyclerView для FAQ
        setupFAQ();

        // Загрузка брендов
        loadBrands();
    }

    private void setupFAQ() {
        List<String> questions = Arrays.asList(
                "Как сделать заказ на сайте?",
                "Какие способы доставки курьером?",
                "Можно ли вернуть товар?",
                "Есть ли у вас программа лояльности?"
        );

        LinkedHashMap<String, String> faqMap = new LinkedHashMap<>();
        faqMap.put(questions.get(0), "Чтобы сделать заказ, выберите нужный товар в каталоге, добавьте его в корзину, нажав на кнопку \"Добавить в корзину\". Затем перейдите в корзину, проверьте выбранные товары и нажмите \"Оформить заказ\". Заполните необходимые данные для доставки и выберите удобный способ оплаты.");
        faqMap.put(questions.get(1), "Мы предлагаем доставку курьером по городу, доставку почтой России, а также самовывоз из нашего магазина. Стоимость и сроки доставки зависят от вашего региона и выбранного способа.");
        faqMap.put(questions.get(2), "Да, вы можете вернуть товар в течение 14 дней с момента покупки, если он не был использован и сохранена упаковка. Для возврата свяжитесь с нашей службой поддержки.");
        faqMap.put(questions.get(3), "Да, у нас действует программа лояльности! За каждый заказ вы получаете бонусные баллы, которые можно использовать для оплаты следующих покупок. Подробности можно узнать в личном кабинете.");

        faqAdapter = new FAQAdapter(this, questions, faqMap);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        faqRecyclerView.setAdapter(faqAdapter);
    }

    private void loadBrands() {
        Call<List<Brand>> call = RetrofitClient.getApiService().getAllBrands();
        call.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brandAdapter = new BrandAdapter(MainPage.this, response.body());
                    brandsRecyclerView.setAdapter(brandAdapter);
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Нет тела ошибки";
                    showToast("Ошибка загрузки брендов: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                showToast("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected int getNavigationItemId() {
        return R.id.nav_home;
    }
}