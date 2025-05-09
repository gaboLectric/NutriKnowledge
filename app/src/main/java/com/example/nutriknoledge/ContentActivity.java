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
        TextView linksView = findViewById(R.id.linksTextView);

        titleView.setText(topicTitle);
        topicContent = description;

        // Crear el texto combinado con formato
        SpannableString combinedText = new SpannableString(description);
        
        // Agregar los puntos principales si existen
        if (points != null && points.length > 0) {
            StringBuilder pointsText = new StringBuilder("\n\nPuntos principales:\n\n");
            for (String point : points) {
                pointsText.append("• ").append(point).append("\n");
            }
            
            // Agregar los puntos principales al final
            combinedText = new SpannableString(description + "\n\n" + pointsText.toString());
            
            // Aplicar formato en negrita y color claro a los puntos principales
            int startBold = description.length() + 3; // "\n\n" + "Puntos principales:\n\n"
            int endBold = combinedText.length();
            
            // Aplicar el formato en negrita
            combinedText.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 
                startBold, endBold, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            
            // Aplicar color blanco a los puntos principales
            combinedText.setSpan(new android.text.style.ForegroundColorSpan(Color.WHITE),
                startBold, endBold, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Configurar el TextView con el texto combinado
        contentView.setText(combinedText);
        contentView.setTextIsSelectable(true);
        
        // Configurar selección de texto
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
                } else if (item.getItemId() == R.id.action_remove_highlight) {
                    selectionStart = contentView.getSelectionStart();
                    selectionEnd = contentView.getSelectionEnd();
                    removeHighlight();
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

        // Mostrar los enlaces si existen
        if (!TextUtils.isEmpty(links)) {
            linksView.setText(links);
        } else {
            linksView.setVisibility(View.GONE);
        }

        // Restaurar highlights si existen
        List<HighlightUtils.HighlightItem> highlights = HighlightUtils.getHighlights(prefs);
        SpannableString spannableString = new SpannableString(contentView.getText());
        
        // Mantener el formato en negrita de los puntos principales
        if (points != null && points.length > 0) {
            int startBold = description.length() + 3; // "\n\n" + "Puntos principales:\n\n"
            int endBold = spannableString.length();
            spannableString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 
                startBold, endBold, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (HighlightUtils.HighlightItem highlight : highlights) {
            if (highlight.topicTitle.equals(topicTitle)) {
                String text = spannableString.toString();
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

    private void removeHighlight() {
        // Obtener el texto actual
        SpannableString spannableString = new SpannableString(contentView.getText());
        
        // Obtener todos los spans de resaltado en el rango seleccionado
        BackgroundColorSpan[] spans = spannableString.getSpans(selectionStart, selectionEnd, BackgroundColorSpan.class);
        
        // Eliminar cada span de resaltado
        for (BackgroundColorSpan span : spans) {
            int start = spannableString.getSpanStart(span);
            int end = spannableString.getSpanEnd(span);
            spannableString.removeSpan(span);
        }
        
        // Actualizar el texto
        contentView.setText(spannableString);
        
        // Eliminar el resaltado de la lista de highlights
        String selectedText = contentView.getText().toString().substring(selectionStart, selectionEnd);
        List<HighlightUtils.HighlightItem> highlights = HighlightUtils.getHighlights(prefs);
        for (HighlightUtils.HighlightItem highlight : highlights) {
            if (highlight.topicTitle.equals(topicTitle) && highlight.text.equals(selectedText)) {
                highlights.remove(highlight);
                HighlightUtils.saveHighlights(prefs, highlights);
                break;
            }
        }
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
            String color = "#B3FFD700"; // Amarillo dorado transparente
            String type = "highlight";
            
            if (v == yellowButton) {
                color = "#B3FFD700"; // Amarillo dorado transparente
            } else if (v == greenButton) {
                color = "#B398FB98"; // Verde menta transparente
            } else if (v == pinkButton) {
                color = "#B3FFB6C1"; // Rosa salmón transparente
            } else if (v == blueButton) {
                color = "#B387CEEB"; // Azul cielo transparente
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