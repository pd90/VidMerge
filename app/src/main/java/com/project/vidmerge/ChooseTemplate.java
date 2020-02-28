package com.project.vidmerge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class ChooseTemplate extends AppCompatActivity {
    ImageView iv_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_template);

        iv_bg = findViewById(R.id.iv_bg);
        int[] bg_images = new int[]{R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3};

//        iv_bg.setImageResource(bg_images[new Random().nextInt(3)]);

        TextView tv_template = findViewById(R.id.tv_template);
        tv_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseTemplate.this, Menu.class));
            }
        });

    }
}
