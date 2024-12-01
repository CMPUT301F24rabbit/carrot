package com.example.goldencarrot.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;

import java.util.List;

public class AdminPosterGalleryActivity extends AppCompatActivity {
    private RecyclerView posterRecyclerView;
    private PosterAdapter posterAdapter;
    private List<String> posterUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_poster_gallery);

        posterRecyclerView = findViewById(R.id.posterRecyclerView);
        posterRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        posterUrls = getIntent().getStringArrayListExtra("posterUrls");
        posterAdapter = new PosterAdapter(this, posterUrls);
        posterRecyclerView.setAdapter(posterAdapter);
    }
}
