package com.project.vidmerge;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogoFragment extends Fragment{

    VideoView vv_logo;
    ImageView iv_logo;
    AnimationDrawable logo_anim_drawable;
    public LogoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logo, container, false);

        String logo_path = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.logo;
        iv_logo = view.findViewById(R.id.iv_logo);
        vv_logo = view.findViewById(R.id.vv_logo);
        vv_logo.setZOrderMediaOverlay(true);
        loadVideologo(logo_path);
        logoAnimation();



        return view;
    }



    public void loadVideologo(String path){
        vv_logo.setVideoURI(Uri.parse(path));

        vv_logo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        vv_logo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
//                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        vv_logo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                System.out.println("setOnPreparedListener:::");
                vv_logo.start();
            }
        });

    }

    public void logoAnimation(){
        try {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_logo.setBackgroundResource(R.drawable.logo_loading);
                    logo_anim_drawable = (AnimationDrawable) iv_logo.getBackground();
                    logo_anim_drawable.start();
                }
            }, 1000);


            //waterAnimCounter=1;

//        Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                if(waterAnimCounter<4) {
//                    iv_water_movement.setImageResource(waterFrames[waterAnimCounter]);
//                    waterAnimCounter++;
//                }
//                else
//                    waterAnimCounter=0;
//
//                waterAnimation();
//            }
//        };
//
//        handler.postDelayed(runnable, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(getContext(), "onDestroy", Toast.LENGTH_SHORT).show();
        iv_logo.clearAnimation();
        super.onDestroy();
    }
}
