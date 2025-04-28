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
        
        List<TopicAdapter.TopicItem> topics = new ArrayList<>();
        topics.add(new TopicAdapter.TopicItem(
            "Principios básicos de la nutrición",
            "Estudia los fundamentos de la nutrición, incluyendo: macronutrientes (carbohidratos, proteínas, grasas), micronutrientes (vitaminas, minerales), hidratación y requerimientos diarios. Subtemas: Guías alimentarias, balance energético, funciones y fuentes de nutrientes."
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Anatomía y fisiología",
            "Comprende los sistemas del cuerpo humano y su relación con la nutrición. Subtemas: sistema digestivo, circulatorio, endocrino, absorción y metabolismo de nutrientes, órganos clave en la nutrición."
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Bioquímica y metabolismo",
            "Analiza los procesos químicos de la digestión, absorción y metabolismo energético. Subtemas: glucólisis, ciclo de Krebs, metabolismo de lípidos y proteínas, regulación hormonal."
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Ciencia de los alimentos",
            "Explora la composición de los alimentos, métodos de preparación, conservación y seguridad alimentaria. Subtemas: etiquetado nutricional, aditivos, tecnología de alimentos, higiene y toxicología."
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Nutrición clínica",
            "Conoce la terapia nutricional médica y las intervenciones dietéticas para enfermedades. Subtemas: dietas terapéuticas, nutrición hospitalaria, evaluación del estado nutricional, soporte nutricional especializado."
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Nutrición comunitaria",
            "Estudia la nutrición en salud pública y programas comunitarios. Subtemas: intervenciones alimentarias, educación nutricional, políticas públicas, programas escolares y comunitarios."
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Gestión de servicios de alimentos",
            "Aprende sobre la operación de comedores, restaurantes y servicios institucionales. Subtemas: planificación de menús, normas de higiene y seguridad, gestión de recursos, control de calidad."
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Métodos de investigación en nutrición",
            "Comprende la metodología de investigación y la práctica basada en evidencia. Subtemas: tipos de estudios, estadística básica, interpretación de resultados, revisión de literatura científica."
        ));
        
        TopicAdapter adapter = new TopicAdapter(this, topics);
        menuRecyclerView.setAdapter(adapter);
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
