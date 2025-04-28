package com.example.nutriknoledge;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GlossaryActivity extends AppCompatActivity {
    private List<GlossaryItem> glossaryItems = new ArrayList<>();
    private List<Object> glossaryDisplayList = new ArrayList<>(); // Puede contener String (header) o GlossaryItem
    private GlossaryAdapter adapter;
    private GlossaryAdapter.OnItemClickListener glossaryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        RecyclerView recyclerView = findViewById(R.id.glossaryRecyclerView);
        EditText searchBar = findViewById(R.id.searchBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar términos desde assets/glosario_demo.csv
        loadGlossaryFromCSV();

        glossaryListener = new GlossaryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GlossaryItem item) {
                // Mostrar detalle (puedes abrir un diálogo o nueva actividad)
                GlossaryDetailDialog.show(GlossaryActivity.this, item);
            }
            @Override
            public void onFavoriteClick(GlossaryItem item) {
                item.setFavorite(!item.isFavorite());
                adapter.notifyDataSetChanged();
            }
        };
        adapter = new GlossaryAdapter(glossaryDisplayList, glossaryListener);
        recyclerView.setAdapter(adapter);


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        List<GlossaryItem> filtered = new ArrayList<>();
        for (GlossaryItem item : glossaryItems) {
            if (item.getTerm().toLowerCase().contains(text.toLowerCase()) ||
                item.getDefinition().toLowerCase().contains(text.toLowerCase())) {
                filtered.add(item);
            }
        }
        List<Object> displayList = buildDisplayList(filtered);
        adapter = new GlossaryAdapter(displayList, glossaryListener);
        RecyclerView recyclerView = findViewById(R.id.glossaryRecyclerView);
        recyclerView.setAdapter(adapter);
    }

    // Leer términos desde assets/glosario_demo.csv
    private void loadGlossaryFromCSV() {
        glossaryItems.clear();
        try {
            java.io.InputStream is = getAssets().open("glosario_demo.csv");
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // Saltar cabecera
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    glossaryItems.add(new GlossaryItem(parts[0], parts[1], parts[2]));
                }
            }
            reader.close();
            // Ordenar por categoría y luego alfabéticamente por término
            java.util.Collections.sort(glossaryItems, new java.util.Comparator<GlossaryItem>() {
                @Override
                public int compare(GlossaryItem o1, GlossaryItem o2) {
                    int cat = o1.getCategory().compareToIgnoreCase(o2.getCategory());
                    return (cat != 0) ? cat : o1.getTerm().compareToIgnoreCase(o2.getTerm());
                }
            });
            glossaryDisplayList.clear();
            glossaryDisplayList.addAll(buildDisplayList(glossaryItems));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Filtro por letra inicial (A-Z)
    private void filterByInitial(char initial) {
        List<GlossaryItem> filtered = new ArrayList<>();
        for (GlossaryItem item : glossaryItems) {
            if (item.getTerm().toUpperCase().startsWith(("" + initial).toUpperCase())) {
                filtered.add(item);
            }
        }
        List<Object> displayList = buildDisplayList(filtered);
        adapter = new GlossaryAdapter(displayList, glossaryListener);
        RecyclerView recyclerView = findViewById(R.id.glossaryRecyclerView);
        recyclerView.setAdapter(adapter);
    }

    // Filtro por categoría (preparado, no UI)
    private void filterByCategory(String category) {
        List<GlossaryItem> filtered = new ArrayList<>();
        for (GlossaryItem item : glossaryItems) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                filtered.add(item);
            }
        }
        List<Object> displayList = buildDisplayList(filtered);
        adapter = new GlossaryAdapter(displayList, glossaryListener);
        RecyclerView recyclerView = findViewById(R.id.glossaryRecyclerView);
        recyclerView.setAdapter(adapter);
    }


    // Construye una lista combinada de headers y términos agrupados por categoría
    private List<Object> buildDisplayList(List<GlossaryItem> items) {
        List<Object> displayList = new ArrayList<>();
        String lastCategory = null;
        for (GlossaryItem item : items) {
            if (lastCategory == null || !item.getCategory().equalsIgnoreCase(lastCategory)) {
                displayList.add(item.getCategory());
                lastCategory = item.getCategory();
            }
            displayList.add(item);
        }
        return displayList;
    }
}

