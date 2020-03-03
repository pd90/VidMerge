package com.project.vidmerge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Random;

import static java.sql.Types.NULL;

public class Menu extends AppCompatActivity {

//    VideoView vv1, vv2, vv3;
    LinearLayout ll1, ll2,ll3,ll4,ll5;
    ImageView iv_1, iv_2, iv_3,iv_4,iv_5;
    TextView tv_continue;
    ImageView iv_bg;
    int selected_template=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

//        vv1 = findViewById(R.id.vv1);
//        vv2 = findViewById(R.id.vv2);
//        vv3 = findViewById(R.id.vv3);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);
        ll4 = findViewById(R.id.ll4);
        ll5 = findViewById(R.id.ll5);
        iv_1 = findViewById(R.id.iv_1);
        iv_2 = findViewById(R.id.iv_2);
        iv_3= findViewById(R.id.iv_3);
        iv_4= findViewById(R.id.iv_4);
        iv_5= findViewById(R.id.iv_5);
        tv_continue= findViewById(R.id.tv_continue);

        setSelectedTemplate();

        iv_bg = findViewById(R.id.iv_bg);
        int[] bg_images = new int[]{R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3};

        iv_bg.setImageResource(bg_images[new Random().nextInt(3)]);

//        String path1 = "android.resource://" + getPackageName() + "/" + R.raw.blue_bg;
//        String path2 = "android.resource://" + getPackageName() + "/" + R.raw.green_bg;
//        String path3 = "android.resource://" + getPackageName() + "/" + R.raw.purple_bg;
//
//        vv1.setVideoURI(Uri.parse(path1));
//        vv2.setVideoURI(Uri.parse(path2));
//        vv3.setVideoURI(Uri.parse(path3));
//        vv1.start();
//        vv2.start();
//        vv3.start();

        iv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_template=1;
                setSelectedTemplate();
//                startActivity(new Intent(Menu.this, MainActivity.class).putExtra("TYPE",1));
//                finish();
            }
        });
        iv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_template=2;
                setSelectedTemplate();
//                startActivity(new Intent(Menu.this, MainActivity.class).putExtra("TYPE",2));
//                finish();
            }
        });
        iv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_template=3;
                setSelectedTemplate();
//                startActivity(new Intent(Menu.this, MainActivity.class).putExtra("TYPE",3));
//                finish();
            }
        });
        iv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_template=4;
                setSelectedTemplate();
//                startActivity(new Intent(Menu.this, MainActivity.class).putExtra("TYPE",3));
//                finish();
            }
        });
        iv_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_template=5;
                setSelectedTemplate();
//                startActivity(new Intent(Menu.this, MainActivity.class).putExtra("TYPE",3));
//                finish();
            }
        });
        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Menu.this, ChooseResolution.class).putExtra("TYPE",selected_template));
//                finish();
            }
        });

    }

    public void setSelectedTemplate(){
        if(selected_template==1) {
            ll1.setBackgroundResource(R.drawable.selection_bg);
            ll2.setBackgroundResource(NULL);
            ll3.setBackgroundResource(NULL);
            ll4.setBackgroundResource(NULL);
            ll5.setBackgroundResource(NULL);
        }else if(selected_template==2) {
            ll1.setBackgroundResource(NULL);
            ll2.setBackgroundResource(R.drawable.selection_bg);
            ll3.setBackgroundResource(NULL);
            ll4.setBackgroundResource(NULL);
            ll5.setBackgroundResource(NULL);
        }else if(selected_template==3) {
            ll1.setBackgroundResource(NULL);
            ll2.setBackgroundResource(NULL);
            ll3.setBackgroundResource(R.drawable.selection_bg);
            ll4.setBackgroundResource(NULL);
            ll5.setBackgroundResource(NULL);
        }
        else if(selected_template==4) {
            ll1.setBackgroundResource(NULL);
            ll2.setBackgroundResource(NULL);
            ll3.setBackgroundResource(NULL);
            ll4.setBackgroundResource(R.drawable.selection_bg);
            ll5.setBackgroundResource(NULL);
        }
        else if(selected_template==5) {
            ll1.setBackgroundResource(NULL);
            ll2.setBackgroundResource(NULL);
            ll3.setBackgroundResource(NULL);
            ll4.setBackgroundResource(NULL);
            ll5.setBackgroundResource(R.drawable.selection_bg);
        }
    }
}
