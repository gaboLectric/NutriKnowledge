package com.example.nutriknoledge;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.widget.AdapterView;
import android.content.Intent;

public class HighlightsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlights);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Highlights");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = findViewById(R.id.highlightsList);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        List<HighlightUtils.HighlightItem> highlights = HighlightUtils.getHighlights(prefs);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (HighlightUtils.HighlightItem h : highlights) {
            String display = h.text + "\n[" + ("underline".equals(h.type) ? "Underline" : "Highlight") + "]\n" + h.topicTitle;
            adapter.add(display);
        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                HighlightUtils.HighlightItem highlight = highlights.get(position);
                new AlertDialog.Builder(HighlightsActivity.this)
                    .setTitle("Highlight Options")
                    .setMessage(highlight.text)
                    .setPositiveButton("Go to Text", (dialog, which) -> {
                        Intent intent = new Intent(HighlightsActivity.this, ContentActivity.class);
                        intent.putExtra("highlight_text", highlight.text);
                        intent.putExtra("highlight_color", highlight.color);
                        intent.putExtra("TOPIC_TITLE", highlight.topicTitle);
                        intent.putExtra("TOPIC_CONTENT", highlight.topicContent);
                        intent.putExtra("TOPIC_URL", highlight.topicUrl);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .setNeutralButton("Delete", (dialog, which) -> {
                        highlights.remove(position);
                        HighlightUtils.saveHighlights(prefs, highlights);
                        adapter.clear();
                        for (HighlightUtils.HighlightItem h : highlights) {
                            String display = h.text + "\n[" + ("underline".equals(h.type) ? "Underline" : "Highlight") + "]\n" + h.topicTitle;
                            adapter.add(display);
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .show();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
