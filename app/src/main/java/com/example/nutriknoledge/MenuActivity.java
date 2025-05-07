package com.example.nutriknoledge;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView menuRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuRecyclerView = findViewById(R.id.menuRecyclerView);

        // Set up navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up navigation view
        navigationView.setNavigationItemSelectedListener(this);

        // Set up recycler view
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        try {
            // Leer el archivo JSON
            InputStream is = getAssets().open("Bd.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();

            // Parsear el JSON
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<TopicData>>(){}.getType();
            List<TopicData> topics = gson.fromJson(jsonString.toString(), listType);

            // Configurar el adaptador
            TopicAdapter adapter = new TopicAdapter(this, topics);
            menuRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, mostrar una lista vacía
            TopicAdapter adapter = new TopicAdapter(this, new ArrayList<>());
            menuRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String themeTitle = null;
        String themeContent = null;
        String themeUrl = null;

        if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_highlights) {
            startActivity(new Intent(this, HighlightsActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_glossary) {
            startActivity(new Intent(this, GlossaryActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
