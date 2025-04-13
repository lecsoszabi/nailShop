package com.example.nailshop;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

public class ShopActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        mAuth = FirebaseAuth.getInstance();

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut(); // Kijelentkezés Firebase-ből
            startActivity(new Intent(ShopActivity.this, LoginActivity.class)); // Vissza a bejelentkezési oldalra
            finish(); // Bezárja az aktuális Activity-t
        });
    }
}
