package com.example.nutriknoledge;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.ActionMode;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import java.util.List;
import com.example.nutriknoledge.SelectableTextView;

public class ContentActivity extends AppCompatActivity {
    private String topicTitle;
    private String topicContent;
    private String topicUrl;
    private PopupWindow highlightActionBar;
    private String lastSelectedText = null;
    private int lastSelStart = -1, lastSelEnd = -1;

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
        SelectableTextView contentView = findViewById(R.id.themeContent);
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

        // Usa el nuevo SelectableTextView para detectar selección
        contentView.setCustomSelectionActionModeCallback(new android.view.ActionMode.Callback() {
            @Override public boolean onCreateActionMode(android.view.ActionMode mode, android.view.Menu menu) { return false; }
            @Override public boolean onPrepareActionMode(android.view.ActionMode mode, android.view.Menu menu) { return false; }
            @Override public boolean onActionItemClicked(android.view.ActionMode mode, android.view.MenuItem item) { return false; }
            @Override public void onDestroyActionMode(android.view.ActionMode mode) {}
        });
        contentView.setOnSelectionChangedListener((selStart, selEnd) -> {
            // Solo mostrar la barra si hay selección, pero NO limpiar la selección automáticamente
            if (selStart != selEnd && selStart >= 0 && selEnd >= 0) {
                lastSelStart = selStart;
                lastSelEnd = selEnd;
                lastSelectedText = contentView.getText().subSequence(selStart, selEnd).toString();
                if (highlightActionBar == null || !highlightActionBar.isShowing()) {
                    showHighlightActionBar(contentView);
                }
            }
            // NO cerrar la barra si se pierde la selección, solo se cierra tras acción o toque fuera
        });

        // Highlight all matching highlights for this content
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String content = contentView.getText().toString();
        List<HighlightUtils.HighlightItem> highlights = HighlightUtils.getHighlightsForContent(prefs, content);
        if (!highlights.isEmpty()) {
            SpannableString spannable = new SpannableString(content);
            for (HighlightUtils.HighlightItem h : highlights) {
                if (h.text == null || h.text.isEmpty()) continue; // skip empty
                int start = content.indexOf(h.text);
                while (start >= 0) {
                    int end = start + h.text.length();
                    if (end > content.length()) break; // safety check
                    if ("underline".equals(h.type)) {
                        spannable.setSpan(new UnderlineSpan(), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        int color = android.graphics.Color.parseColor(h.color);
                        spannable.setSpan(new BackgroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    // Prevent infinite loop if h.text is not found again
                    int nextStart = content.indexOf(h.text, end);
                    if (nextStart == start) break;
                    start = nextStart;
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

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_highlight) {
                int selStart = contentView.getSelectionStart();
                int selEnd = contentView.getSelectionEnd();
                if (selStart != selEnd) {
                    final CharSequence selectedText = contentView.getText().subSequence(Math.min(selStart, selEnd), Math.max(selStart, selEnd));
                    String[] options = {"Yellow Highlight", "Green Highlight", "Pink Highlight", "Underline"};
                    String[] colorVals = {"#FFF9C4", "#C8E6C9", "#F8BBD0", "#000000"};
                    new AlertDialog.Builder(ContentActivity.this)
                        .setTitle("Choose highlight type")
                        .setItems(options, (dialog, which) -> {
                            if (which == 3) {
                                HighlightUtils.addHighlight(prefs, selectedText.toString(), "#000000", topicTitle, topicContent, topicUrl, "underline");
                            } else {
                                HighlightUtils.addHighlight(prefs, selectedText.toString(), colorVals[which], topicTitle, topicContent, topicUrl, "highlight");
                            }
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
        // No inflamos ningún menú para eliminar los botones extra
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Solo dejamos la navegación hacia atrás
        return super.onOptionsItemSelected(item);
    }

    private void applyHighlights(SelectableTextView contentView, SharedPreferences prefs) {
        String content = contentView.getText().toString();
        List<HighlightUtils.HighlightItem> highlights = HighlightUtils.getHighlightsForContent(prefs, content);
        SpannableString spannable = new SpannableString(content);
        boolean isDark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        for (HighlightUtils.HighlightItem h : highlights) {
            if (h.text == null || h.text.isEmpty()) continue; // skip empty
            int start = content.indexOf(h.text);
            while (start >= 0) {
                int end = start + h.text.length();
                if (end > content.length()) break; // safety check
                if ("underline".equals(h.type)) {
                    int underlineColor = android.graphics.Color.parseColor("#33FFF9C4"); // 20% transparent yellow
                    if (!isDark) underlineColor = android.graphics.Color.parseColor("#FFF9C4");
                    spannable.setSpan(new BackgroundColorSpan(underlineColor), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new UnderlineSpan(), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    int color;
                    if (isDark) {
                        if (h.color.equals("#FFF9C4") || h.color.equalsIgnoreCase("@color/highlight_bg")) {
                            color = android.graphics.Color.parseColor("#33FFF9C4"); // 20% transparent yellow
                        } else if (h.color.equals("#C8E6C9")) {
                            color = android.graphics.Color.parseColor("#33C8E6C9"); // 20% transparent green
                        } else if (h.color.equals("#F8BBD0")) {
                            color = android.graphics.Color.parseColor("#33F8BBD0"); // 20% transparent pink
                        } else {
                            color = android.graphics.Color.parseColor(h.color);
                        }
                    } else {
                        color = android.graphics.Color.parseColor(h.color);
                    }
                    spannable.setSpan(new BackgroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                int nextStart = content.indexOf(h.text, end);
                if (nextStart == start) break;
                start = nextStart;
            }
        }
        contentView.setText(spannable);
    }

    private void showHighlightActionBar(SelectableTextView anchor) {
        if (highlightActionBar != null && highlightActionBar.isShowing()) highlightActionBar.dismiss();
        LayoutInflater inflater = LayoutInflater.from(this);
        View bar = inflater.inflate(R.layout.highlight_action_bar, null, false);
        highlightActionBar = new PopupWindow(bar, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // Permitir cerrar la barra al tocar fuera
        highlightActionBar.setOutsideTouchable(true);
        highlightActionBar.setFocusable(true);
        highlightActionBar.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        highlightActionBar.setOnDismissListener(() -> {
            // Limpia la selección solo si la barra se cierra manualmente
            Selection.setSelection((Spannable) anchor.getText(), 0, 0);
        });
        // Setup color actions
        int[] colorIds = {R.id.color_yellow, R.id.color_green, R.id.color_blue, R.id.color_orange, R.id.color_pink, R.id.color_teal, R.id.color_purple, R.id.color_red, R.id.color_brown, R.id.color_gray};
        String[] colorValues = {"#33FFF9C4", "#33219EBC", "#338ECAE6", "#33FB8500", "#33F8BBD0", "#3300BFAE", "#334B3F72", "#33E57373", "#33A1887F", "#339E9E9E"};
        for (int i = 0; i < colorIds.length; i++) {
            int idx = i;
            bar.findViewById(colorIds[i]).setOnClickListener(v -> {
                if (lastSelectedText != null && lastSelStart >= 0 && lastSelEnd > lastSelStart) {
                    highlightText(anchor, lastSelStart, lastSelEnd, colorValues[idx]);
                    // Feedback visual
                    v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(120).withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
                        if (highlightActionBar != null && highlightActionBar.isShowing()) highlightActionBar.dismiss();
                        // Limpia la selección SOLO después de la acción
                        Selection.setSelection((Spannable) anchor.getText(), 0, 0);
                    }).start();
                }
            });
        }
        // Copy button
        ImageButton copyBtn = bar.findViewById(R.id.btn_copy);
        copyBtn.setOnClickListener(v -> {
            if (lastSelectedText != null) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", lastSelectedText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Texto copiado", Toast.LENGTH_SHORT).show();
                // Feedback visual
                v.animate().rotation(20f).setDuration(80).withEndAction(() -> {
                    v.animate().rotation(0f).setDuration(80).start();
                    if (highlightActionBar != null && highlightActionBar.isShowing()) highlightActionBar.dismiss();
                    // Limpia la selección SOLO después de la acción
                    Selection.setSelection((Spannable) anchor.getText(), 0, 0);
                }).start();
            }
        });
        // Position below selection
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        highlightActionBar.setElevation(12f);
        highlightActionBar.showAtLocation(anchor, android.view.Gravity.TOP | android.view.Gravity.START, location[0] + 32, location[1] + anchor.getHeight() + 10);
    }

    private void highlightText(SelectableTextView contentView, int start, int end, String color) {
        Spannable spannable = new SpannableString(contentView.getText());
        spannable.setSpan(new BackgroundColorSpan(android.graphics.Color.parseColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentView.setText(spannable);
        // Save highlight (reuse existing logic)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String safeTopicTitle = this.topicTitle != null ? this.topicTitle : "";
        HighlightUtils.addHighlight(prefs, contentView.getText().subSequence(start, end).toString(), color, safeTopicTitle, topicContent, topicUrl, "highlight");
        applyHighlights(contentView, prefs);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
