package com.example.cinebook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinebook.R;
import com.example.cinebook.network.Review;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final List<Review> news;

    public NewsAdapter(List<Review> news) {
        this.news = news;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Review review = news.get(position);
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView content;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.news_author);
            content = itemView.findViewById(R.id.news_content);
        }
    }
}