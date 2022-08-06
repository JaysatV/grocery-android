package com.rice.mandi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rice.mandi.Retrofit.RetrofitApiClient;

public class FullScreenImageActivity extends AppCompatActivity {

    ImageView FullScreenImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        FullScreenImg = findViewById(R.id.full_screen_img);
        Glide.with(this).
                load(getIntent().getStringExtra("img_url"))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(FullScreenImg);
    }
}