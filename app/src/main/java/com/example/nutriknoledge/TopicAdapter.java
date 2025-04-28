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
    private final Context context;
    private final List<TopicItem> topics;

    public static class TopicItem {
        String title;
        String description;

        public TopicItem(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    public TopicAdapter(Context context, List<TopicItem> topics) {
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
        TopicItem topic = topics.get(position);
        holder.titleText.setText(topic.title);
        holder.descriptionText.setText(topic.description);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ContentActivity.class);
            intent.putExtra("TOPIC_TITLE", topic.title);
            intent.putExtra("TOPIC_CONTENT", topic.description);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView descriptionText;

        TopicViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.topicTitle);
            descriptionText = itemView.findViewById(R.id.topicDescription);
        }
    }
}
