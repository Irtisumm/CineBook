package com.example.cinebook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.moviedetails.MovieDetail;
import com.example.cinebook.network.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final List<Movie> movies;
    private final Context context;
    private final boolean isComingSoon; // New flag for Coming Soon

    // Constructor for regular movies
    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
        this.isComingSoon = false; // Default to false
    }

    // Constructor for Coming Soon movies
    public MovieAdapter(Context context, List<Movie> movies, boolean isComingSoon) {
        this.context = context;
        this.movies = movies;
        this.isComingSoon = isComingSoon;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.title.setText(movie.getTitle());
        holder.overview.setText(movie.getOverview());
        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        android.util.Log.d("MovieAdapter", "Loading poster: " + posterUrl);
        Glide.with(context)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            if (isComingSoon) {
                Toast.makeText(context, "This movie is coming soon!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(context, MovieDetail.class);
                intent.putExtra("movie", movie);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        TextView overview;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster_image);
            title = itemView.findViewById(R.id.movie_title);
            overview = itemView.findViewById(R.id.movie_overview);
        }
    }
}