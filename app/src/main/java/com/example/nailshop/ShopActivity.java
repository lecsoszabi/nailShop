package com.example.nailshop;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
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

public class ShopActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerProducts;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private FirebaseFirestore db;

    // Modern Activity Result API
    ActivityResultLauncher<Intent> addProductLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- Modern Activity Result API beállítása ---
        addProductLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadProductsFromFirestore();
                    }
                }
        );

        // Gomb: Új termék hozzáadása
        Button btnAddProduct = findViewById(R.id.btnAddTestProduct);
        btnAddProduct.setText("Új termék hozzáadása");
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, AddOrEditProductActivity.class);
            addProductLauncher.launch(intent); // Modern módon indítjuk!
        });

        recyclerProducts = findViewById(R.id.recyclerProducts);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, productList);
        recyclerProducts.setAdapter(adapter);

        // Kijelentkezés gomb
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

        // Első betöltéskor is töltsük be a termékeket
        loadProductsFromFirestore();
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
