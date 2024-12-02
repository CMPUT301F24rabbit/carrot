package com.example.goldencarrot.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.example.goldencarrot.R;

import java.util.Random;

/**
 * Controller for managing Background
 * Randomly assigns one view to the background of the app.
 */
public class RanBackground {
    private static final int[] BACKGROUND_RESOURCES = {
            R.drawable.bg1,
            R.drawable.bg2,
            R.drawable.bg3,
            R.drawable.bg4,
            R.drawable.bg5,
            R.drawable.bg6,
            R.drawable.bg7,
            R.drawable.bg8,
            R.drawable.bg9,
            R.drawable.bg10,
            R.drawable.bg11,
            R.drawable.bg12,
            R.drawable.bg13,
    };

    // Get random background, or gray if in night mode
    public static Drawable getRandomBackground(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            return ContextCompat.getDrawable(context, R.color.dark_mode_background); // Create a color resource for gray background
        } else {
            // Return a random background for light mode
            Random random = new Random();
            int randomIndex = random.nextInt(BACKGROUND_RESOURCES.length);
            return context.getDrawable(BACKGROUND_RESOURCES[randomIndex]);
        }
    }
}
