package com.example.nutriknoledge;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Collections;
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
        // Show most recent first, underlines first
        Collections.reverse(highlights);
        Collections.sort(highlights, (a, b) -> {
            if ("underline".equals(b.type) && !"underline".equals(a.type)) return 1;
            if ("underline".equals(a.type) && !"underline".equals(b.type)) return -1;
            return 0;
        });
        HighlightsAdapter adapter = new HighlightsAdapter(this, highlights);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
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
                    adapter.notifyDataSetChanged();
                })
                .show();
        });
    }

    static class HighlightsAdapter extends BaseAdapter {
        private final List<HighlightUtils.HighlightItem> items;
        private final LayoutInflater inflater;
        HighlightsAdapter(android.content.Context ctx, List<HighlightUtils.HighlightItem> items) {
            this.items = items;
            this.inflater = LayoutInflater.from(ctx);
        }
        @Override
        public int getCount() { return items.size(); }
        @Override
        public Object getItem(int position) { return items.get(position); }
        @Override
        public long getItemId(int position) { return position; }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) v = inflater.inflate(R.layout.highlight_item, parent, false);
            HighlightUtils.HighlightItem item = items.get(position);
            TextView topic = v.findViewById(R.id.highlight_topic);
            TextView text = v.findViewById(R.id.highlight_text);
            topic.setText(item.topicTitle);
            text.setText(item.text);
            // Quitar color de fondo amarillo y usar color de tema (claro/oscuro)
            v.setBackgroundResource(R.color.highlight_bg);
            // Opcional: puedes ajustar el color del texto si el highlight era amarillo
            // if (item.color != null && item.color.equals("#FFF9C4")) {
            //     text.setTextColor(ContextCompat.getColor(v.getContext(), R.color.on_surface));
            // }
            return v;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
