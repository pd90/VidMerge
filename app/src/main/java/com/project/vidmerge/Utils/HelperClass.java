package com.project.vidmerge.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

public class HelperClass {
    public static float height;
    public static float width;
    public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    public static final String VIDEO_TOTAL_DURATION = "VIDEO_TOTAL_DURATION";
    public static String video_path ;

    public static int alphabets_type=2;

    public static void getPhoneHeightWidth(Context context){
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((AppCompatActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = (float) displayMetrics.heightPixels;
            width = (float) displayMetrics.widthPixels;
            System.out.println("height: "+height);
            System.out.println("width: "+width);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
