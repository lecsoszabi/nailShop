package com.example.nailshop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList = new ArrayList<>();
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        MaterialToolbar toolbar = findViewById(R.id.cartToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItemList);
        recyclerView.setAdapter(cartAdapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Toast.makeText(this, "Nem vagy bejelentkezve!", Toast.LENGTH_SHORT).show();
            return;
        }


        Button btnOrder = findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(v -> {
            db.collection("carts")
                    .document(userId)
                    .collection("items")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<CartItem> items = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            CartItem item = doc.toObject(CartItem.class);
                            items.add(item);
                        }

                        if (items.isEmpty()) {
                            Toast.makeText(this, "A kosár üres!", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Order order = new Order(userId, System.currentTimeMillis(), items);

                        db.collection("orders")
                                .document(userId)
                                .collection("orders")
                                .add(order)
                                .addOnSuccessListener(orderRef -> {
                                    // 4. Kosár törlése
                                    for (CartItem item : items) {
                                        db.collection("carts")
                                                .document(userId)
                                                .collection("items")
                                                .document(item.getProductId())
                                                .delete();
                                    }
                                    Toast.makeText(this, "Rendelés sikeresen leadva!", Toast.LENGTH_SHORT).show();
                                    cartItemList.clear();
                                    cartAdapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Hiba a rendelés leadásakor!", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hiba a kosár lekérdezésekor!", Toast.LENGTH_SHORT).show();
                    });
        });
        loadCartItems();
    }

    private void loadCartItems() {
        db.collection("carts")
                .document(userId)
                .collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        cartItemList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            CartItem item = doc.toObject(CartItem.class);
                            cartItemList.add(item);
                        }
                        cartAdapter.notifyDataSetChanged();
                        if (cartItemList.isEmpty()) {
                            Toast.makeText(this, "A kosár üres.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CartActivity.this, "Hiba a kosár betöltésekor!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
