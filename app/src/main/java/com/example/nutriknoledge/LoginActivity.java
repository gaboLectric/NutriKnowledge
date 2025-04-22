package com.example.nutriknoledge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private EditText nameInput;
    private MaterialButton loginButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Check if user already logged in
        String savedEmail = prefs.getString("email", "");
        if (!savedEmail.isEmpty()) {
            // Skip login if user info exists
            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        nameInput = findViewById(R.id.nameInput);

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
        String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save login data locally
        prefs.edit().putString("email", email).putString("name", name).apply();

        // Continue to menu
        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
        finish();
    }
}
