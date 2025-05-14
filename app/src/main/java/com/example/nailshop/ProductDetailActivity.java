package com.example.nailshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);


        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String productId = getIntent().getStringExtra("productId");
            String name = getIntent().getStringExtra("productName");
            double price = getIntent().getDoubleExtra("productPrice", 0);

            CartItem cartItem = new CartItem(userId, productId, name, price, 1);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("carts")
                    .document(userId)
                    .collection("items")
                    .document(productId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Már van ilyen termék, mennyiséget növelünk
                            CartItem existingItem = documentSnapshot.toObject(CartItem.class);
                            int newQuantity = existingItem.getQuantity() + 1;
                            db.collection("carts")
                                    .document(userId)
                                    .collection("items")
                                    .document(productId)
                                    .update("quantity", newQuantity)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Mennyiség növelve a kosárban!", Toast.LENGTH_SHORT).show();
                                        new android.os.Handler().postDelayed(() -> {
                                            Intent intent = new Intent(ProductDetailActivity.this, ShopActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                            finish();
                                        }, 1000);
                                    });
                        } else {
                            // Nincs még ilyen termék, újként adjuk hozzá
                            db.collection("carts")
                                    .document(userId)
                                    .collection("items")
                                    .document(productId)
                                    .set(cartItem)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Sikeresen a kosárba került a termék!", Toast.LENGTH_SHORT).show();
                                        new android.os.Handler().postDelayed(() -> {
                                            Intent intent = new Intent(ProductDetailActivity.this, ShopActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                            finish();
                                        }, 1000);
                                    });
                        }
                    });
        });

        TextView tvName = findViewById(R.id.tvProductName);
        TextView tvDescription = findViewById(R.id.tvProductDescription);
        TextView tvPrice = findViewById(R.id.tvProductPrice);

        String name = getIntent().getStringExtra("productName");
        String description = getIntent().getStringExtra("productDescription");
        double price = getIntent().getDoubleExtra("productPrice", 0);

        tvName.setText(name);
        tvDescription.setText(description);
        tvPrice.setText(price + " Ft");
    }
}
