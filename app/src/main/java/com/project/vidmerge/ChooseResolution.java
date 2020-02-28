package com.project.vidmerge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;
import static java.sql.Types.NULL;

public class ChooseResolution extends AppCompatActivity {

    LinearLayout ll1, ll2;
    TextView tv_continue, tv_1, tv_2;
    ImageView iv_bg;
    int selected_resolution=1;
    int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_resolution);

        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_continue= findViewById(R.id.tv_continue);

        setSelectedTemplate();

        iv_bg = findViewById(R.id.iv_bg);
        int[] bg_images = new int[]{R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3};
        if(getIntent()!=null){
            type = getIntent().getIntExtra("TYPE", 0);
        }

        iv_bg.setImageResource(bg_images[new Random().nextInt(3)]);

        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_resolution=1;
                setSelectedTemplate();
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_resolution=2;
                setSelectedTemplate();
            }
        });
        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseResolution.this, MainActivity.class).putExtra("TYPE",type).putExtra("RES_TYPE",selected_resolution));
            }
        });

    }

    public void setSelectedTemplate(){
        if(selected_resolution==1) {
            ll1.setBackgroundResource(R.drawable.selection_bg);
            ll2.setBackgroundResource(NULL);
        }else if(selected_resolution==2) {
            ll1.setBackgroundResource(NULL);
            ll2.setBackgroundResource(R.drawable.selection_bg);
        }
    }
}

