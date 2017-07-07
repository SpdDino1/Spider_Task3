package com.example.vikramkumaresan.v1;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Loading_Screen extends AppCompatActivity {
    ImageView img;
    public static Loading_Screen ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading__screen);

        ctx=this;
        img= (ImageView)findViewById(R.id.img);
        Glide.with(this).load(R.mipmap.loading).into(img);
    }
}
