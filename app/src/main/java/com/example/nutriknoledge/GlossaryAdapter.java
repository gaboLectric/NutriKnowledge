package com.example.nutriknoledge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GlossaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_TERM = 1;

    public interface OnItemClickListener {
        void onItemClick(GlossaryItem item);
        void onFavoriteClick(GlossaryItem item);
    }

    private final List<Object> items; // Puede ser String (categoría) o GlossaryItem
    private final OnItemClickListener listener;

    public GlossaryAdapter(List<Object> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof String) ? TYPE_CATEGORY : TYPE_TERM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_header, parent, false);
            return new CategoryViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_glossary, parent, false);
            return new GlossaryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            ((CategoryViewHolder) holder).categoryHeader.setText((String) items.get(position));
        } else if (holder instanceof GlossaryViewHolder) {
            GlossaryItem item = (GlossaryItem) items.get(position);
            GlossaryViewHolder gHolder = (GlossaryViewHolder) holder;
            gHolder.termText.setText(item.getTerm());
            gHolder.categoryText.setText(item.getCategory());
            gHolder.favoriteButton.setImageResource(item.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            gHolder.itemView.setOnClickListener(v -> listener.onItemClick(item));
            gHolder.favoriteButton.setOnClickListener(v -> listener.onFavoriteClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class GlossaryViewHolder extends RecyclerView.ViewHolder {
        TextView termText, categoryText;
        ImageButton favoriteButton;
        public GlossaryViewHolder(@NonNull View itemView) {
            super(itemView);
            termText = itemView.findViewById(R.id.glossaryTerm);
            categoryText = itemView.findViewById(R.id.glossaryCategory);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryHeader;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryHeader = itemView.findViewById(R.id.categoryHeader);
        }
    }
}
