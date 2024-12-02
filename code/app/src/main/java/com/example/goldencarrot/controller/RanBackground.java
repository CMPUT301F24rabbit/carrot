package com.example.goldencarrot.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.goldencarrot.R;

import java.util.Random;

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

    public static Drawable getRandomBackground(Context context) {
        Random random = new Random();
        int randomIndex = random.nextInt(BACKGROUND_RESOURCES.length);
        return context.getDrawable(BACKGROUND_RESOURCES[randomIndex]);
    }
}
