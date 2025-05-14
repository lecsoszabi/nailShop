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

    private boolean editMode = false;
    private String productId = null;

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

        // Ellenőrizzük, hogy szerkesztés módban vagyunk-e
        Intent intent = getIntent();
        editMode = intent.getBooleanExtra("editMode", false);
        productId = intent.getStringExtra("productId");

        if (editMode && productId != null) {
            // Betöltjük a meglévő termék adatait
            db.collection("products")
                    .document(productId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Product product = documentSnapshot.toObject(Product.class);
                            if (product != null) {
                                etName.setText(product.getName());
                                etDescription.setText(product.getDescription());
                                etPrice.setText(String.valueOf(product.getPrice()));
                                etImageUrl.setText(product.getImageUrl());
                            }
                        }
                    });
        }

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
            String uploaderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String finalProductId = (editMode && productId != null)
                    ? productId
                    : db.collection("products").document().getId();

            Product product = new Product(finalProductId, name, description, price, imageUrl, userId, uploaderEmail);

            db.collection("products")
                    .document(finalProductId)
                    .set(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this,
                                editMode ? "Termék sikeresen módosítva!" : "Termék sikeresen mentve!",
                                Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hiba a mentéskor!", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
