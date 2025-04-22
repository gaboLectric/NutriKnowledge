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
            "Basic Nutrition Principles",
            "Learn about macronutrients, micronutrients, and the foundations of nutrition science"
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Anatomy and Physiology",
            "Understand the human body systems and their relationship with nutrition"
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Biochemistry and Metabolism",
            "Study the chemical processes of digestion, absorption, and metabolism"
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Food Science",
            "Explore food composition, preparation methods, and food safety"
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Clinical Nutrition",
            "Learn about medical nutrition therapy and dietary interventions"
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Community Nutrition",
            "Study public health nutrition and community-based interventions"
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Food Service Management",
            "Learn about food service operations and institutional nutrition"
        ));
        topics.add(new TopicAdapter.TopicItem(
            "Research Methods",
            "Understand nutrition research methodology and evidence-based practice"
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
        } else if (id == R.id.nav_theme_1) {
            themeTitle = "Theme 1";
            themeContent = "This is a detailed description for Theme 1. Here you will learn about the basics of nutrition and its importance in daily life.";
            themeUrl = "https://en.wikipedia.org/wiki/Nutrition";
        } else if (id == R.id.nav_theme_2) {
            themeTitle = "Theme 2";
            themeContent = "Theme 2 covers the anatomy and physiology relevant to nutrition science.";
            themeUrl = "https://en.wikipedia.org/wiki/Anatomy";
        } else if (id == R.id.nav_theme_3) {
            themeTitle = "Theme 3";
            themeContent = "Theme 3 is about biochemistry and metabolism.";
            themeUrl = "https://en.wikipedia.org/wiki/Biochemistry";
        } else if (id == R.id.nav_theme_4) {
            themeTitle = "Theme 4";
            themeContent = "Theme 4 explores food science and technology.";
            themeUrl = "https://en.wikipedia.org/wiki/Food_science";
        } else if (id == R.id.nav_theme_5) {
            themeTitle = "Theme 5";
            themeContent = "Theme 5 focuses on clinical nutrition and patient care.";
            themeUrl = "https://en.wikipedia.org/wiki/Clinical_nutrition";
        } else if (id == R.id.nav_theme_6) {
            themeTitle = "Theme 6";
            themeContent = "Theme 6 introduces community nutrition programs.";
            themeUrl = "https://en.wikipedia.org/wiki/Community_nutrition";
        } else if (id == R.id.nav_theme_7) {
            themeTitle = "Theme 7";
            themeContent = "Theme 7 covers food service management.";
            themeUrl = "https://en.wikipedia.org/wiki/Food_service";
        } else if (id == R.id.nav_theme_8) {
            themeTitle = "Theme 8";
            themeContent = "Theme 8 is about research methods in nutrition.";
            themeUrl = "https://en.wikipedia.org/wiki/Research_methods";
        } else if (id == R.id.nav_theme_9) {
            themeTitle = "Theme 9";
            themeContent = "Theme 9 explores sports nutrition and performance.";
            themeUrl = "https://en.wikipedia.org/wiki/Sports_nutrition";
        } else if (id == R.id.nav_theme_10) {
            themeTitle = "Theme 10";
            themeContent = "Theme 10 covers pediatric nutrition.";
            themeUrl = "https://en.wikipedia.org/wiki/Pediatric_nutrition";
        } else if (id == R.id.nav_theme_11) {
            themeTitle = "Theme 11";
            themeContent = "Theme 11 is about geriatric nutrition.";
            themeUrl = "https://en.wikipedia.org/wiki/Geriatrics";
        } else if (id == R.id.nav_theme_12) {
            themeTitle = "Theme 12";
            themeContent = "Theme 12 covers nutrition assessment techniques.";
            themeUrl = "https://en.wikipedia.org/wiki/Nutrition_assessment";
        } else if (id == R.id.nav_theme_13) {
            themeTitle = "Theme 13";
            themeContent = "Theme 13 explores diet planning and counseling.";
            themeUrl = "https://en.wikipedia.org/wiki/Diet_planning";
        } else if (id == R.id.nav_theme_14) {
            themeTitle = "Theme 14";
            themeContent = "Theme 14 focuses on micronutrients and their role.";
            themeUrl = "https://en.wikipedia.org/wiki/Micronutrient";
        } else if (id == R.id.nav_theme_15) {
            themeTitle = "Theme 15";
            themeContent = "Theme 15 discusses macronutrients in detail.";
            themeUrl = "https://en.wikipedia.org/wiki/Macronutrient";
        } else if (id == R.id.nav_theme_16) {
            themeTitle = "Theme 16";
            themeContent = "Theme 16 is about public health nutrition policies.";
            themeUrl = "https://en.wikipedia.org/wiki/Public_health";
        } else if (id == R.id.nav_theme_17) {
            themeTitle = "Theme 17";
            themeContent = "Theme 17 explores food allergies and intolerances.";
            themeUrl = "https://en.wikipedia.org/wiki/Food_allergy";
        } else if (id == R.id.nav_theme_18) {
            themeTitle = "Theme 18";
            themeContent = "Theme 18 covers eating disorders and their management.";
            themeUrl = "https://en.wikipedia.org/wiki/Eating_disorder";
        } else if (id == R.id.nav_theme_19) {
            themeTitle = "Theme 19";
            themeContent = "Theme 19 discusses nutrition in chronic diseases.";
            themeUrl = "https://en.wikipedia.org/wiki/Chronic_disease";
        } else if (id == R.id.nav_theme_20) {
            themeTitle = "Theme 20";
            themeContent = "Theme 20 is about global nutrition challenges.";
            themeUrl = "https://en.wikipedia.org/wiki/Global_nutrition";
        }

        if (themeTitle != null) {
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra("TOPIC_TITLE", themeTitle);
            intent.putExtra("TOPIC_CONTENT", themeContent);
            intent.putExtra("TOPIC_URL", themeUrl);
            startActivity(intent);
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
