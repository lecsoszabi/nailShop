package com.example.nailshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

// Például egy gomb vagy bármilyen view animálásához:


public class ShopActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerProducts;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadProductsFromFirestore();
        // Teszt termék gomb (bal felső sarok)
        Button btnAddTestProduct = findViewById(R.id.btnAddTestProduct);
        btnAddTestProduct.setOnClickListener(v -> {
            Product product = new Product("99", "Teszt termék", "Ez csak teszt", 1234.0, "");
            db.collection("products").add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ShopActivity.this, "Teszt termék hozzáadva!", Toast.LENGTH_SHORT).show();
                        loadProductsFromFirestore(); // FRISSÍTÉS!
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ShopActivity.this, "Hiba a termék hozzáadásakor!", Toast.LENGTH_SHORT).show();
                    });
        });

        recyclerProducts = findViewById(R.id.recyclerProducts);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, productList);
        recyclerProducts.setAdapter(adapter);



        // Kijelentkezés gomb (jobb felső sarok)
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ShopActivity.this, LoginActivity.class));
            finish();
        });

        Button btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, CartActivity.class));
        });

        Button btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, ProfileActivity.class));
        });



    }

    private void loadProductsFromFirestore() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        productList.clear();
                        QuerySnapshot result = task.getResult();
                        for (QueryDocumentSnapshot document : result) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ShopActivity.this, "Hiba a termékek betöltésekor!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
