package com.example.eyelike;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ImgViewActivity extends AppCompatActivity {
    Uri imageUri;
    ImageView imageView;
    Button start, stop;
    int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_view);
        Intent intent = getIntent();
        imageUri =intent.getParcelableExtra("imageUri");
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
    }

    public void startBtnClicked(View v){

        if(index==0){
            start.setVisibility(v.INVISIBLE);
            stop.setVisibility(v.VISIBLE);
            index=1;
        }else{
            start.setVisibility(v.VISIBLE);
            stop.setVisibility(v.INVISIBLE);
            index=0;
        }
    }
}
