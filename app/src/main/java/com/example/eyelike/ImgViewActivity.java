package com.example.eyelike;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImgViewActivity extends AppCompatActivity {
    Uri imageUri;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_view);
        Intent intent = getIntent();
        imageUri =intent.getParcelableExtra("imageUri");
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
    }
}
