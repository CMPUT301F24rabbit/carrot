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

/**
 * Adapter for displaying a list of posters in a {@link RecyclerView}.
 * This adapter loads poster images from URLs and handles the click event to open
 * the image in a full-screen view.
 */
public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    private Context context;
    private List<String> posterUrls;

    /**
     * Constructs a new PosterAdapter.
     *
     * @param context the context in which the adapter is used
     * @param posterUrls the list of URLs for the posters
     */
    public PosterAdapter(Context context, List<String> posterUrls) {
        this.context = context;
        this.posterUrls = posterUrls;
    }

    /**
     * Creates a new {@link PosterViewHolder} by inflating the poster_item layout.
     *
     * @param parent the parent view group
     * @param viewType the type of view to create
     * @return a new {@link PosterViewHolder}
     */
    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_item, parent, false);
        return new PosterViewHolder(view);
    }

    /**
     * Binds the data from the poster URL list to the poster image view.
     * Also sets an on click listener to open the image in full screen when clicked.
     *
     * @param holder the {@link PosterViewHolder} to bind data to
     * @param position the position of the item in the list
     */
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

    /**
     * Returns the total number of items in the poster list.
     *
     * @return the number of items in the list
     */
    @Override
    public int getItemCount() {
        return posterUrls.size();
    }

    /**
     * ViewHolder class for holding a reference to the poster image view.
     */
    public static class PosterViewHolder extends RecyclerView.ViewHolder {

        ImageView posterImageView;

        /**
         * Constructs a new {@link PosterViewHolder}.
         *
         * @param itemView the view for a single poster item
         */
        public PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
        }
    }
}
