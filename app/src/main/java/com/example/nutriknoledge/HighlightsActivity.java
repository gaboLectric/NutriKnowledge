package com.example.nutriknoledge;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighlightsActivity extends AppCompatActivity {
    private ListView highlightsList;
    private SharedPreferences prefs;
    private List<HighlightUtils.HighlightItem> highlights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlights);

        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        highlightsList = findViewById(R.id.highlightsList);

        loadHighlights();
    }

    private void loadHighlights() {
        highlights = HighlightUtils.getHighlights(prefs);
        
        // Ordenar los highlights: primero los subrayados, luego por fecha
        Collections.sort(highlights, (h1, h2) -> {
            if (h1.type.equals("underline") && !h2.type.equals("underline")) {
                return -1;
            } else if (!h1.type.equals("underline") && h2.type.equals("underline")) {
                return 1;
            }
            return 0;
        });

        highlightsList.setAdapter(new HighlightsAdapter());
    }

    private class HighlightsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return highlights.size();
        }

        @Override
        public HighlightUtils.HighlightItem getItem(int position) {
            return highlights.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(HighlightsActivity.this)
                    .inflate(R.layout.highlight_item, parent, false);
            }

            HighlightUtils.HighlightItem highlight = getItem(position);

            TextView topic = convertView.findViewById(R.id.topicTitleTextView);
            TextView text = convertView.findViewById(R.id.highlightTextView);

            topic.setText(highlight.topicTitle);
            text.setText(highlight.text);

            convertView.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(HighlightsActivity.this);
                builder.setTitle("Opciones")
                    .setItems(new String[]{"Ir al texto", "Cancelar", "Eliminar"}, (dialog, which) -> {
                        if (which == 0) {
                            // Navegar al texto
                            Intent intent = new Intent(HighlightsActivity.this, ContentActivity.class);
                            intent.putExtra("TOPIC_TITLE", highlight.topicTitle);
                            intent.putExtra("TOPIC_DESCRIPTION", highlight.topicContent);
                            intent.putExtra("TOPIC_URL", highlight.topicUrl);
                            startActivity(intent);
                        } else if (which == 2) {
                            highlights.remove(highlight);
                            HighlightUtils.saveHighlights(prefs, highlights);
                            notifyDataSetChanged();
                        }
                    })
                    .show();
            });

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
