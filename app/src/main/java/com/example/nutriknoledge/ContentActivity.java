package com.example.nutriknoledge;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;
import android.graphics.Color;
import java.util.List;
import android.text.Spannable;

public class ContentActivity extends AppCompatActivity {
    private String topicTitle;
    private String topicContent;
    private TextView contentView;
    private ActionMode actionMode;
    private SharedPreferences prefs;
    private String selectedText;
    private int selectionStart;
    private int selectionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtener los datos del intent
        topicTitle = getIntent().getStringExtra("TOPIC_TITLE");
        String description = getIntent().getStringExtra("TOPIC_DESCRIPTION");
        String[] points = getIntent().getStringArrayExtra("TOPIC_POINTS");
        String links = getIntent().getStringExtra("TOPIC_LINKS");

        // Configurar las vistas
        TextView titleView = findViewById(R.id.titleTextView);
        contentView = findViewById(R.id.descriptionTextView);
        TextView pointsView = findViewById(R.id.contentTextView);
        TextView linksView = findViewById(R.id.linksTextView);

        titleView.setText(topicTitle);
        contentView.setText(description);
        topicContent = description;

        // Configurar selección de texto
        contentView.setTextIsSelectable(true);
        contentView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode = mode;
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.text_selection_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.action_highlight) {
                    selectionStart = contentView.getSelectionStart();
                    selectionEnd = contentView.getSelectionEnd();
                    selectedText = contentView.getText().toString().substring(selectionStart, selectionEnd);
                    showColorPickerDialog();
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        });

        // Mostrar los puntos principales si existen
        if (points != null && points.length > 0) {
            StringBuilder pointsText = new StringBuilder("Puntos principales:\n\n");
            for (String point : points) {
                pointsText.append("• ").append(point).append("\n");
            }
            pointsView.setText(pointsText.toString());
        } else {
            pointsView.setVisibility(View.GONE);
        }

        // Mostrar los enlaces si existen
        if (!TextUtils.isEmpty(links)) {
            linksView.setText(links);
        } else {
            linksView.setVisibility(View.GONE);
        }

        // Restaurar highlights si existen
        List<HighlightUtils.HighlightItem> highlights = HighlightUtils.getHighlights(prefs);
        SpannableString spannableString = new SpannableString(contentView.getText());
        for (HighlightUtils.HighlightItem highlight : highlights) {
            if (highlight.topicTitle.equals(topicTitle)) {
                String text = contentView.getText().toString();
                int index = text.indexOf(highlight.text);
                if (index >= 0) {
                    if (highlight.type.equals("underline")) {
                        spannableString.setSpan(new UnderlineSpan(), index, index + highlight.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        spannableString.setSpan(new BackgroundColorSpan(Color.parseColor(highlight.color)), 
                            index, index + highlight.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        contentView.setText(spannableString);
    }

    private void showColorPickerDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.color_picker_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(dialogView)
            .create();

        ImageButton yellowButton = dialogView.findViewById(R.id.colorYellow);
        ImageButton greenButton = dialogView.findViewById(R.id.colorGreen);
        ImageButton pinkButton = dialogView.findViewById(R.id.colorPink);
        ImageButton blueButton = dialogView.findViewById(R.id.colorBlue);
        ImageButton underlineButton = dialogView.findViewById(R.id.colorUnderline);

        View.OnClickListener colorClickListener = v -> {
            String color = "#FFF9C4";
            String type = "highlight";
            
            if (v == yellowButton) {
                color = "#FFF9C4";
            } else if (v == greenButton) {
                color = "#C8E6C9";
            } else if (v == pinkButton) {
                color = "#F8BBD0";
            } else if (v == blueButton) {
                color = "#BBDEFB";
            } else if (v == underlineButton) {
                type = "underline";
                color = "none";
            }

            // Obtener el texto actual como SpannableString
            SpannableString spannableString = new SpannableString(contentView.getText());
            
            // Aplicar el highlight o subrayado
            if (type.equals("underline")) {
                spannableString.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spannableString.setSpan(new BackgroundColorSpan(Color.parseColor(color)), 
                    selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            
            // Guardar el highlight
            String topicUrl = getIntent().getStringExtra("TOPIC_URL");
            HighlightUtils.addHighlight(prefs, selectedText, color, topicTitle, topicContent, topicUrl, type);
            
            // Actualizar el texto
            contentView.setText(spannableString);
            
            dialog.dismiss();
        };

        yellowButton.setOnClickListener(colorClickListener);
        greenButton.setOnClickListener(colorClickListener);
        pinkButton.setOnClickListener(colorClickListener);
        blueButton.setOnClickListener(colorClickListener);
        underlineButton.setOnClickListener(colorClickListener);

        dialog.show();
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