package com.example.nutriknoledge;

import android.os.Bundle;
import android.content.res.AssetManager;
import android.util.Log;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView topicsRecyclerView;
    private TopicAdapter adapter;
    private List<TopicData> topics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar RecyclerView
        topicsRecyclerView = findViewById(R.id.topicsRecyclerView);
        topicsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Cargar datos
        loadTopics();

        // Configurar navegación inferior
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_topics) {
                return true;
            } else if (itemId == R.id.navigation_highlights) {
                startActivity(new Intent(this, HighlightsActivity.class));
                return true;
            } else if (itemId == R.id.navigation_glossary) {
                startActivity(new Intent(this, GlossaryActivity.class));
                return true;
            } else if (itemId == R.id.navigation_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.navigation_topics);
    }

    private void loadTopics() {
        topics = new ArrayList<>();
        AssetManager assetManager = getAssets();
        try {
            // Leer el archivo JSON desde assets
            Log.d(TAG, "Intentando abrir Bd.json");
            InputStream inputStream = assetManager.open("Bd.json");
            InputStreamReader reader = new InputStreamReader(inputStream);

            // Usar Gson para parsear el JSON
            Type listType = new TypeToken<ArrayList<TopicData>>(){}.getType();
            topics = new Gson().fromJson(reader, listType);
            
            Log.d(TAG, "Datos cargados. Número de temas: " + (topics != null ? topics.size() : 0));

            if (topics != null && !topics.isEmpty()) {
                // Crear y configurar el adaptador
                adapter = new TopicAdapter(this, topics);
                topicsRecyclerView.setAdapter(adapter);
                Log.d(TAG, "Adaptador configurado con " + topics.size() + " temas");
            } else {
                Log.e(TAG, "La lista de temas está vacía o es nula");
            }

        } catch (IOException e) {
            Log.e(TAG, "Error loading topics: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado: " + e.getMessage(), e);
        }
    }
}