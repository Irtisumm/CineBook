package com.example.cinebook.moviedetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.network.TmdbCredits;
import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private final Context context;
    private final List<TmdbCredits.Cast> castList;

    public CastAdapter(Context context, List<TmdbCredits.Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cast_item, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        TmdbCredits.Cast cast = castList.get(position);
        holder.nameTextView.setText(cast.getName());

        String profileUrl = cast.getProfilePath() != null ?
                "https://image.tmdb.org/t/p/w185" + cast.getProfilePath() : null;
        Glide.with(context)
                .load(profileUrl)
                .placeholder(R.drawable.circle)
                .error(R.drawable.circle)
                .into(holder.photoImageView);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView nameTextView;

        CastViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.cast_imageView_photo);
            nameTextView = itemView.findViewById(R.id.cast_textView_name);
        }
    }
}