package com.example.nutriknoledge;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class SelectableTextView extends AppCompatTextView {
    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selStart, int selEnd);
    }

    private OnSelectionChangedListener selectionChangedListener;

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SelectableTextView(Context context) {
        super(context);
    }
    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(selStart, selEnd);
        }
    }
}
