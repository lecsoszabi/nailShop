package com.example.nailshop;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private FirebaseFirestore db;
    private String userId;
    private TextView tvProfileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileName = findViewById(R.id.tvProfileName);

        MaterialToolbar toolbar = findViewById(R.id.profileToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Felhasználó nevének vagy emailjének kiírása
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Ha van displayName, azt írjuk ki, egyébként az email-t
            String displayName = user.getDisplayName();
            String email = user.getEmail();
            if (displayName != null && !displayName.isEmpty()) {
                tvProfileName.setText("Felhasználó: " + displayName);
            } else if (email != null) {
                tvProfileName.setText("Email: " + email);
            } else {
                tvProfileName.setText("Ismeretlen felhasználó");
            }
        } else {
            tvProfileName.setText("Nincs bejelentkezve");
        }

        recyclerView = findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        db = FirebaseFirestore.getInstance();
        userId = user != null ? user.getUid() : null;

        if (userId != null) {
            loadOrders();
        }
    }

    private void loadOrders() {
        db.collection("orders")
                .document(userId)
                .collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        orderList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Order order = doc.toObject(Order.class);
                            orderList.add(order);
                        }
                        orderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Hiba a rendelések betöltésekor!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
