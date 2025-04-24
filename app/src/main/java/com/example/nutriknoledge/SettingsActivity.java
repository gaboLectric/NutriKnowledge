package com.example.nutriknoledge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private Switch darkModeSwitch;
    private EditText nameEdit;
    private TextView emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        nameEdit = findViewById(R.id.nameEdit);
        emailView = findViewById(R.id.emailView);

        boolean isNight = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        darkModeSwitch.setChecked(isNight);
        darkModeSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        String email = prefs.getString("email", "");
        String name = prefs.getString("name", "");
        emailView.setText(email);
        nameEdit.setText(name);

        findViewById(R.id.saveBtn).setOnClickListener(v -> {
            String newName = nameEdit.getText().toString().trim();
            prefs.edit().putString("name", newName).apply();
            Toast.makeText(this, "Name updated!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
