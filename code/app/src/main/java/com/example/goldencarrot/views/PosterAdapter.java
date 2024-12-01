package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {
    private Context context;
    private List<String> posterUrls;

    public PosterAdapter(Context context, List<String> posterUrls) {
        this.context = context;
        this.posterUrls = posterUrls;
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_item, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        String posterUrl = posterUrls.get(position);
        Picasso.get().load(posterUrl).into(holder.posterImageView);
        holder.posterImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullScreenImageActivity.class);
            intent.putExtra("imageUrl", posterUrl);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posterUrls.size();
    }

    public static class PosterViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;

        public PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
        }
    }
}

