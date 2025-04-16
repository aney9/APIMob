package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            setupBottomNavigation();
            bottomNavigationView.setSelectedItemId(getNavigationItemId());
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == getNavigationItemId()) {
                return true;
            }
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainPage.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_catalog) {
                startActivity(new Intent(this, CatalogPage.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritePage.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilePage.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(this, CartPage.class));
                finish();
                return true;
            }
            return false;
        });
    }

    protected abstract int getNavigationItemId();
}