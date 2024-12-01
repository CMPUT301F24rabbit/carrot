package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;
import com.example.goldencarrot.views.PosterAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminPosterGalleryActivity extends AppCompatActivity {
    private RecyclerView posterRecyclerView;
    private PosterAdapter posterAdapter;
    private List<String> posterUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_poster_gallery);

        // Initialize RecyclerView
        posterRecyclerView = findViewById(R.id.posterRecyclerView);
        posterRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Get poster URLs from Intent (if passed)
        posterUrls = getIntent().getStringArrayListExtra("posterUrls");
        if (posterUrls == null) {
            posterUrls = new ArrayList<>();  // Initialize empty list if no data is passed
        }

        // Set up the adapter
        posterAdapter = new PosterAdapter(this, posterUrls);
        posterRecyclerView.setAdapter(posterAdapter);
    }

}
