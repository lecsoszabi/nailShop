package com.example.nailshop;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FirebaseAuth inicializálása
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Ellenőrizd, hogy a felhasználó már be van-e jelentkezve
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Ha a felhasználó már be van jelentkezve, irányítsd át a ShopActivity-re
            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
            startActivity(intent);
            finish(); // Bezárja a MainActivity-t
            return; // Ne folytassa tovább az onCreate metódust
        }

        // Bejelentkezési gomb inicializálása
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Regisztrációs gomb inicializálása
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }
}
