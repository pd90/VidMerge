package com.project.vidmerge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Random;

public class Splash extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 4000;
    VideoView vv_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        vv_bg = findViewById(R.id.vv_bg);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.open;
        loadVideoBg(path);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, ChooseTemplate.class));
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void loadVideoBg(String path){
        vv_bg.setVideoURI(Uri.parse(path));

        vv_bg.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        vv_bg.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        vv_bg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                System.out.println("setOnPreparedListener:::");
                vv_bg.start();
            }
        });

    }
}
