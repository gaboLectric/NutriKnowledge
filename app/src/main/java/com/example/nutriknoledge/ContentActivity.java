package com.example.nutriknoledge;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import java.util.List;

public class ContentActivity extends AppCompatActivity {
    private String topicTitle;
    private String topicContent;
    private String topicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_content);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView titleView = findViewById(R.id.themeTitle);
        TextView contentView = findViewById(R.id.themeContent);
        Button readMoreBtn = findViewById(R.id.readMoreBtn);

        topicTitle = getIntent().getStringExtra("TOPIC_TITLE");
        topicContent = getIntent().getStringExtra("TOPIC_CONTENT");
        topicUrl = getIntent().getStringExtra("TOPIC_URL");

        if (topicTitle != null) {
            setTitle(topicTitle);
            titleView.setText(topicTitle);
        }
        if (topicContent != null) {
            contentView.setText(topicContent);
        }
        readMoreBtn.setOnClickListener(v -> {
            if (topicUrl != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(topicUrl));
                startActivity(browserIntent);
            }
        });

        // Highlight all matching highlights for this content
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String content = contentView.getText().toString();
        List<HighlightUtils.HighlightItem> highlights = HighlightUtils.getHighlightsForContent(prefs, content);
        if (!highlights.isEmpty()) {
            SpannableString spannable = new SpannableString(content);
            for (HighlightUtils.HighlightItem h : highlights) {
                int start = content.indexOf(h.text);
                while (start >= 0) {
                    int end = start + h.text.length();
                    int color = android.graphics.Color.parseColor(h.color);
                    spannable.setSpan(new BackgroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = content.indexOf(h.text, end);
                }
            }
            contentView.setText(spannable);
        }

        // If a highlight_text extra is present, scroll to it
        String highlightText = getIntent().getStringExtra("highlight_text");
        String highlightColor = getIntent().getStringExtra("highlight_color");
        if (highlightText != null && !highlightText.isEmpty()) {
            int start = content.indexOf(highlightText);
            if (start >= 0) {
                contentView.post(() -> {
                    android.text.Layout layout = contentView.getLayout();
                    if (layout != null) {
                        int line = layout.getLineForOffset(start);
                        int y = layout.getLineTop(line);
                        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.contentScrollView);
                        scrollView.smoothScrollTo(0, y);
                    }
                });
            }
        }

        contentView.setCustomSelectionActionModeCallback(new android.view.ActionMode.Callback() {
            int min = 0, max = 0;
            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, android.view.Menu menu) {
                return true;
            }
            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, android.view.Menu menu) {
                return false;
            }
            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, android.view.MenuItem item) {
                // Let the default actions (copy/cut/share) proceed
                return false;
            }
            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                if (contentView.isFocused()) {
                    int selStart = contentView.getSelectionStart();
                    int selEnd = contentView.getSelectionEnd();
                    min = Math.max(0, Math.min(selStart, selEnd));
                    max = Math.max(0, Math.max(selStart, selEnd));
                }
                final CharSequence selectedText = contentView.getText().subSequence(min, max);
                if (selectedText.length() > 0) {
                    new AlertDialog.Builder(ContentActivity.this)
                        .setTitle("Highlight text?")
                        .setMessage(selectedText)
                        .setPositiveButton("Highlight", (dialog, which) -> {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ContentActivity.this);
                            HighlightUtils.addHighlight(prefs, selectedText.toString(), "#FFF9C4", topicTitle, topicContent, topicUrl);
                            applyHighlights(contentView, prefs);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_highlight) {
                int selStart = contentView.getSelectionStart();
                int selEnd = contentView.getSelectionEnd();
                if (selStart != selEnd) {
                    final CharSequence selectedText = contentView.getText().subSequence(Math.min(selStart, selEnd), Math.max(selStart, selEnd));
                    String[] colors = {"Yellow", "Green", "Pink"};
                    String[] colorVals = {"#FFF9C4", "#C8E6C9", "#F8BBD0"};
                    new AlertDialog.Builder(ContentActivity.this)
                        .setTitle("Choose highlight color")
                        .setItems(colors, (dialog, which) -> {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ContentActivity.this);
                            HighlightUtils.addHighlight(prefs, selectedText.toString(), colorVals[which], topicTitle, topicContent, topicUrl);
                            applyHighlights(contentView, prefs);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                } else {
                    Toast.makeText(ContentActivity.this, "Select text to highlight", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_highlight) {
            TextView contentView = findViewById(R.id.themeContent);
            int selStart = contentView.getSelectionStart();
            int selEnd = contentView.getSelectionEnd();
            if (selStart != selEnd) {
                final CharSequence selectedText = contentView.getText().subSequence(Math.min(selStart, selEnd), Math.max(selStart, selEnd));
                String[] colors = {"Yellow", "Green", "Pink"};
                String[] colorVals = {"#FFF9C4", "#C8E6C9", "#F8BBD0"};
                new AlertDialog.Builder(ContentActivity.this)
                    .setTitle("Choose highlight color")
                    .setItems(colors, (dialog, which) -> {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ContentActivity.this);
                        HighlightUtils.addHighlight(prefs, selectedText.toString(), colorVals[which], topicTitle, topicContent, topicUrl);
                        applyHighlights(contentView, prefs);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            } else {
                Toast.makeText(ContentActivity.this, "Select text to highlight", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyHighlights(TextView contentView, SharedPreferences prefs) {
        String content = contentView.getText().toString();
        List<HighlightUtils.HighlightItem> highlights = HighlightUtils.getHighlightsForContent(prefs, content);
        SpannableString spannable = new SpannableString(content);
        for (HighlightUtils.HighlightItem h : highlights) {
            int start = content.indexOf(h.text);
            while (start >= 0) {
                int end = start + h.text.length();
                int color = android.graphics.Color.parseColor(h.color);
                spannable.setSpan(new BackgroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = content.indexOf(h.text, end);
            }
        }
        contentView.setText(spannable);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
