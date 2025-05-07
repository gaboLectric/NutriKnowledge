package com.example.nutriknoledge;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private List<TopicData> topics;
    private Context context;

    public TopicAdapter(Context context, List<TopicData> topics) {
        this.context = context;
        this.topics = topics;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicData topic = topics.get(position);
        holder.titleTextView.setText(topic.getTitulo());
        holder.descriptionTextView.setText(topic.getDescripcionGeneral());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ContentActivity.class);
            intent.putExtra("TOPIC_TITLE", topic.getTitulo());
            intent.putExtra("TOPIC_DESCRIPTION", topic.getDescripcionGeneral());
            
            // Convertir la lista de puntos principales a un array
            List<String> points = topic.getPuntosPrincipales();
            if (points != null && !points.isEmpty()) {
                String[] pointsArray = points.toArray(new String[0]);
                intent.putExtra("TOPIC_POINTS", pointsArray);
            }

            // Formatear los enlaces
            List<TopicData.LinkData> links = topic.getLinksRecomendados();
            if (links != null && !links.isEmpty()) {
                StringBuilder linksText = new StringBuilder("Enlaces recomendados:\n\n");
                for (TopicData.LinkData link : links) {
                    linksText.append("• ").append(link.getTituloLink())
                            .append("\n  ").append(link.getUrl()).append("\n\n");
                }
                intent.putExtra("TOPIC_LINKS", linksText.toString());
            }

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topics != null ? topics.size() : 0;
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        TopicViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
