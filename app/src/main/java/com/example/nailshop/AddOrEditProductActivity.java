package com.example.nailshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddOrEditProductActivity extends AppCompatActivity {
    private EditText etName, etDescription, etPrice, etImageUrl;
    private Button btnSave;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_product);


        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());
        etName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etProductDescription);
        etPrice = findViewById(R.id.etProductPrice);
        etImageUrl = findViewById(R.id.etProductImageUrl);
        btnSave = findViewById(R.id.btnSaveProduct);

        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(this, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Az ár csak szám lehet!", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String productId = db.collection("products").document().getId();

            Product product = new Product(productId, name, description, price, imageUrl, userId);

            db.collection("products")
                    .document(productId)
                    .set(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Termék sikeresen mentve!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Ezzel jelezzük a ShopActivity-nek, hogy frissíteni kell!
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hiba a mentéskor!", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
