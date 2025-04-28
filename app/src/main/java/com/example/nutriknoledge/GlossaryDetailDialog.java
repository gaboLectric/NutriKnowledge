package com.example.nutriknoledge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class GlossaryDetailDialog extends Dialog {
    private GlossaryItem item;
    public GlossaryDetailDialog(Context context, GlossaryItem item) {
        super(context);
        this.item = item;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_glossary_detail);
        ((TextView)findViewById(R.id.detailTerm)).setText(item.getTerm());
        ((TextView)findViewById(R.id.detailDefinition)).setText(item.getDefinition());
        ((TextView)findViewById(R.id.detailCategory)).setText(item.getCategory());
    }
    public static void show(Context context, GlossaryItem item) {
        GlossaryDetailDialog dialog = new GlossaryDetailDialog(context, item);
        dialog.show();
    }
}
