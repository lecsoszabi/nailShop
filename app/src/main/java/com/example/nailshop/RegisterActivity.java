package com.example.nailshop;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.example.nailshop.databinding.ActivityRegisterBinding;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Regisztráció gomb kezelése
        binding.registerButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Hiba: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(RegisterActivity.this, "Tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
            }
        });

        // Visszagomb kezelése
        binding.backButton.setOnClickListener(v -> {
            // Vissza a főoldalra (MainActivity)
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Bezárja a Regisztrációs képernyőt
        });
    }
}
