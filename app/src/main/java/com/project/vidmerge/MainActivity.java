package com.project.vidmerge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.project.vidmerge.Utils.CircleSurface;
import com.project.vidmerge.Utils.GetFilePathFromDevice;
import com.project.vidmerge.Utils.HelperClass;
import com.project.vidmerge.Utils.ProgressCalculator;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.sql.Types.NULL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FFmpeg fFmpeg;
    VideoView vv_bg, vv1, vv2, vv3, vv4;
    MediaPlayer mp1, mp2, mp3, mp4;
    String path_vv1, path_vv2, path_vv3, path_vv4;
    ImageView iv_select_new_file, iv_mute, iv_trim, iv_create_collage, iv_swap;
    long timeInMillis_v1, timeInMillis_v2, timeInMillis_v3, timeInMillis_v4;
    LinearLayout ll1, ll2, ll3, ll4, ll_menu, ll_options;
    int selected_vv=0;
    boolean[] videosMuteStatus = new boolean[]{false, false, false, false};
    boolean isToAddWatermark1=false,isToAddWatermark2=false,isToAddWatermark3=false,isToAddWatermark4=false;
    boolean isToSetResolution=false;
    boolean isSwapOn=false;
    int viewWhichTurnsOnSwap=0;
    public static boolean isVideoTrimmed=false;
    TextView tv_merge, tv_cancel;
//    ProgressDialog progressDialogResWatMark;
    ProgressDialog progressBar;
    CircularProgressBar circularProgressBar;
    private int ATTACH_FILE_REQUEST_CODE = 20;
    private int PICK_FILE_REQUEST_CODE = 11;

    //upload file
    static String filePath = "";
    int fileSize;
    File myFile;
    String displayName = "";

    String base64file="";
    String extension_splited = "";
    int attached_fileSize;
    String attached_fileName;
    BackMediaController mControl1, mControl2, mControl3, mControl4;
    int type, resolution_type;

    public Button iv_mute_button1,iv_un_mute_button1,iv_mute_button2,iv_un_mute_button2,
            iv_mute_button3,iv_un_mute_button3,iv_mute_button4,iv_un_mute_button4;
    ProgressCalculator mProgressCalculator;
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    public static final int NONE = 3;
    public static final int RUNNING = 4;
    private int status = NONE;
    int height,width;
//    CircleSurface surface;
//    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int density =displayMetrics.densityDpi;
        Log.e("density",String.valueOf(density));
        height = displayMetrics.heightPixels/2;
        width  = displayMetrics.widthPixels;
        Log.e("height",String.valueOf(height));
        Log.e("width",String.valueOf(width));
        isVideoTrimmed=false;
        HelperClass.getPhoneHeightWidth(this);
        try {
            loadFFmpegLib();
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
        vv_bg = findViewById(R.id.vv_bg);
//        vv_logo = findViewById(R.id.vv_logo);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_merge = findViewById(R.id.tv_merge);
        vv1 = findViewById(R.id.vv1);
        vv2 = findViewById(R.id.vv2);
        vv3 = findViewById(R.id.vv3);
        vv4 = findViewById(R.id.vv4);
        ll1 = findViewById(R.id.ll1);
        // set params layout 1
       // circularProgressBar = findViewById(R.id.circularProgressBar);

        Log.e("height",String.valueOf(height - 150));

        ll2 = findViewById(R.id.ll2);

        ll3 = findViewById(R.id.ll3);

        ll4 = findViewById(R.id.ll4);

        ll_menu = findViewById(R.id.ll_menu);
        ll_options = findViewById(R.id.ll_options);
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) ll1.getLayoutParams();
        LinearLayout.LayoutParams params2= (LinearLayout.LayoutParams) ll2.getLayoutParams();
        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) ll3.getLayoutParams();
        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams) ll4.getLayoutParams();
        if(density>320&&density<480) {
            params1.height = height - 180;
            params2.height = height - 180;
            params3.height = height - 180;
            params4.height = height - 180;
        }else if(density>DisplayMetrics.DENSITY_HIGH&&density<320){
            params1.height = height - 100;
            params2.height = height - 100;
            params3.height = height - 100;
            params4.height = height - 100;
        }
        else if(density<DisplayMetrics.DENSITY_HIGH&&density>DisplayMetrics.DENSITY_MEDIUM){
            params1.height = height - 50;
            params2.height = height - 50;
            params3.height = height - 50;
            params4.height = height - 50;
        }
        ll1.setLayoutParams(params1);
        ll2.setLayoutParams(params2);
        ll3.setLayoutParams(params3);
        ll4.setLayoutParams(params4);
        iv_swap = findViewById(R.id.iv_swap);
        iv_select_new_file = findViewById(R.id.iv_select_new_file);
        iv_mute = findViewById(R.id.iv_mute);
        iv_trim = findViewById(R.id.iv_trim);
        iv_create_collage = findViewById(R.id.iv_create_collage);

//        vv_logo.setZOrderMediaOverlay(true);
        vv1.setZOrderMediaOverlay(true);
        vv2.setZOrderMediaOverlay(true);
        vv3.setZOrderMediaOverlay(true);
        vv4.setZOrderMediaOverlay(true);


        tv_cancel.setOnClickListener(this);
        iv_swap.setOnClickListener(this);
        iv_select_new_file.setOnClickListener(this);
        iv_mute.setOnClickListener(this);
        // mute buttons
        iv_mute_button1 = findViewById(R.id.iv_mute_button_1);
        iv_un_mute_button1 = findViewById(R.id.iv_un_mute_button_1);

        iv_mute_button2 = findViewById(R.id.iv_mute_button_2);
        iv_un_mute_button2 = findViewById(R.id.iv_un_mute_button_2);

        iv_mute_button3 = findViewById(R.id.iv_mute_button_3);
        iv_un_mute_button3 = findViewById(R.id.iv_un_mute_button_3);

        iv_mute_button4 = findViewById(R.id.iv_mute_button_4);
        iv_un_mute_button4 = findViewById(R.id.iv_un_mute_button_4);
        iv_mute_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!videosMuteStatus[0]) {
                    try {
                        executeRemoveAudiFromVideoCommand(path_vv1);
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                    videosMuteStatus[0] = true;
                    iv_un_mute_button1.setVisibility(View.VISIBLE);
                    iv_mute_button1.setVisibility(View.GONE);
                }
            }
        });
        iv_un_mute_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videosMuteStatus[0]) {
                    try {
                        executeWaterMarkCommandVideo1(filePath);
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                    videosMuteStatus[0] = false;
                    iv_un_mute_button1.setVisibility(View.GONE);
                    iv_mute_button1.setVisibility(View.VISIBLE);
                }
            }
        });

        //button 2
        iv_mute_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!videosMuteStatus[1]) {
                    try {
                        executeRemoveAudiFromVideoCommand(path_vv2);
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                    videosMuteStatus[1] = true;
                    iv_un_mute_button2.setVisibility(View.VISIBLE);
                    iv_mute_button2.setVisibility(View.GONE);
                }
            }
        });
        iv_un_mute_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videosMuteStatus[1]) {
                    try {
                        executeWaterMarkCommandVideo2(filePath);
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                    videosMuteStatus[1] = false;
                    iv_un_mute_button2.setVisibility(View.GONE);
                    iv_mute_button2.setVisibility(View.VISIBLE);
                }
            }
        });
        //button 3
        iv_mute_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!videosMuteStatus[2]) {
                    try {
                        executeRemoveAudiFromVideoCommand(path_vv3);
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                    videosMuteStatus[2] = true;
                    iv_un_mute_button3.setVisibility(View.VISIBLE);
                    iv_mute_button3.setVisibility(View.GONE);
                }
            }
        });
        iv_un_mute_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videosMuteStatus[2]) {
                    try {
                        executeWaterMarkCommandVideo3(filePath);
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                    videosMuteStatus[2] = false;
                    iv_un_mute_button3.setVisibility(View.GONE);
                    iv_mute_button3.setVisibility(View.VISIBLE);
                }
            }
        });

        //button 4
        iv_mute_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!videosMuteStatus[3]) {
                    try {
                        executeRemoveAudiFromVideoCommand(path_vv4);
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                    videosMuteStatus[3] = true;
                    iv_un_mute_button4.setVisibility(View.VISIBLE);
                    iv_mute_button4.setVisibility(View.GONE);
                }
            }
        });
        iv_un_mute_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videosMuteStatus[3]) {
                    try {
                        executeWaterMarkCommandVideo4(filePath);
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                    videosMuteStatus[3] = false;
                    iv_un_mute_button4.setVisibility(View.GONE);
                    iv_mute_button4.setVisibility(View.VISIBLE);
                }
            }
        });
        iv_trim.setOnClickListener(this);
        iv_create_collage.setOnClickListener(this);
        tv_merge.setOnClickListener(this);
        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        ll4.setOnClickListener(this);

        hideOptions();
        mp1 = new MediaPlayer();
        mp2 = new MediaPlayer();
        mp3 = new MediaPlayer();
        mp4 = new MediaPlayer();

        if(getIntent()!=null){
            type = getIntent().getIntExtra("TYPE", 0);
            resolution_type = getIntent().getIntExtra("RES_TYPE", 0);
        }

        String path ="";
        if(type==1)
            path = "android.resource://" + getPackageName() + "/" + R.raw.blue_new;
        else if(type==2)
            path = "android.resource://" + getPackageName() + "/" + R.raw.green_bg;
        else if(type==3)
            path = "android.resource://" + getPackageName() + "/" + R.raw.purple_bg;

         String logo_path = "android.resource://" + getPackageName() + "/" + R.raw.logo;
//        vv_bg.setVideoURI(Uri.parse(path));
//        vv_bg.start();

        vv_bg.getLayoutParams().height = (int) (HelperClass.height);
        vv_bg.getLayoutParams().width = (int) (HelperClass.width);
        vv_bg.requestLayout();

        loadVideoBg(path);
//        loadVideologo(logo_path);
////        surface = (CircleSurface) findViewById(R.id.vv_logo);
////        surface.setZOrderMediaOverlay(true);
//        SurfaceHolder holder = surface.getHolder();
//        holder.addCallback(this);
//
//        player = MediaPlayer.create(this, R.raw.logo);

        //replaceFragment(new LogoFragment());

//        setLayoutsSize();
//        try {
//            executeDecreaseSizeCommand();
////            executeAddBackroundVideoCommand();
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            e.printStackTrace();
//        }


    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(MainActivity.this, Menu.class));
//        finish();
//        super.onBackPressed();
//    }

    //    public void setLayoutsSize(){
//        vv1.getLayoutParams().height = (int) (HelperClass.height/2);
//        vv1.getLayoutParams().width = (int) (HelperClass.width/2);
//
//        vv2.getLayoutParams().height = (int) (HelperClass.height/2);
//        vv2.getLayoutParams().width = (int) (HelperClass.width/2);
//
//        vv3.getLayoutParams().height = (int) (HelperClass.height/2);
//        vv3.getLayoutParams().width = (int) (HelperClass.width/2);
//
//        vv4.getLayoutParams().height = (int) (HelperClass.height/2);
//        vv4.getLayoutParams().width = (int) (HelperClass.width/2);
//
//
//        vv1.requestLayout();
//        vv2.requestLayout();
//        vv3.requestLayout();
//        vv4.requestLayout();
//
//    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.logo_frame, fragment);
//        fragmentTransaction.addToBackStack(fragment.toString());
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ATTACH_FILE_REQUEST_CODE);
            return;
        }
        storeFileOnSdcard();
        try {
        switch (id){
            case R.id.iv_swap:
                if(!isSwapOn)
                swapVideos();
                break;
            case R.id.tv_cancel:
                isSwapOn = false;
                tv_cancel.setVisibility(View.GONE);
                if(isOptionsToShow(selected_vv))
                    ll_options.setVisibility(View.VISIBLE);
                else
                    hideOptions();
                break;
            case R.id.iv_select_new_file:
                chooseFile();
                break;
            case R.id.iv_mute:
                if(selected_vv==1){
                    if(!videosMuteStatus[0]) {
                        executeRemoveAudiFromVideoCommand(path_vv1);
//                        mute(mp1);
                        videosMuteStatus[0] = true;
                    } else {
//                        unmute(mp1);
//                        videosMuteStatus[0] = false;
                    }
                }else if(selected_vv==2){
                    if(!videosMuteStatus[1]) {
                        executeRemoveAudiFromVideoCommand(path_vv2);
//                        mute(mp2);
                        videosMuteStatus[1] = true;
                    } else {
//                        unmute(mp2);
//                        videosMuteStatus[1] = false;
                    }
                }else if(selected_vv==3){
                    if(!videosMuteStatus[2]) {
                        executeRemoveAudiFromVideoCommand(path_vv3);
//                        mute(mp3);
                        videosMuteStatus[2] = true;
                    } else {
//                        unmute(mp3);
//                        videosMuteStatus[2] = false;
                    }
                }else if(selected_vv==4){
                    if(!videosMuteStatus[3]) {
                        executeRemoveAudiFromVideoCommand(path_vv4);
//                        mute(mp4);
                        videosMuteStatus[3] = true;
                    } else {
//                        unmute(mp4);
//                        videosMuteStatus[3] = false;
                    }
                }
                break;
            case R.id.iv_trim:
                if(selected_vv==1)
                    goToTrimActivity(path_vv1, timeInMillis_v1);
                else if(selected_vv==2)
                    goToTrimActivity(path_vv2, timeInMillis_v2);
                else if(selected_vv==3)
                    goToTrimActivity(path_vv3, timeInMillis_v3);
                else if(selected_vv==4)
                    goToTrimActivity(path_vv4, timeInMillis_v4);
                break;
            case R.id.iv_create_collage:
            case R.id.tv_merge:
                    if(isVideosCollageable()) {
                        Intent intent = new Intent(MainActivity.this, CreateFinalCollage.class);
                        intent.putExtra("PATH1", path_vv1);
                        intent.putExtra("PATH2", path_vv2);
                        intent.putExtra("PATH3", path_vv3);
                        intent.putExtra("PATH4", path_vv4);
                        intent.putExtra("TYPE", type);
                        startActivity(intent);
                        finish();
//                        executeSaveOverlayedVideoCommand();
                    }
                    else
                    Toast.makeText(getApplicationContext(), "Please select all videos.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll1:
                selected_vv=1;
                setLayoutSelected();
                if(isSwapOn && viewWhichTurnsOnSwap!=selected_vv){
                    if(!TextUtils.isEmpty(path_vv1))
                        performSwaping();
                }else {
                    hideOptions();
                    if (TextUtils.isEmpty(path_vv1) || path_vv1 == null)
                        chooseFile();
                    else
                        displayOptions();
//                    selectOptionDialog();
                }
                break;
            case R.id.ll2:
                selected_vv= 2;
                setLayoutSelected();
                if(isSwapOn && viewWhichTurnsOnSwap!=selected_vv){
                    if(!TextUtils.isEmpty(path_vv2))
                        performSwaping();
                }else {
                    hideOptions();
                    if (TextUtils.isEmpty(path_vv2) || path_vv2 == null)
                        chooseFile();
                    else
                        displayOptions();
//                    selectOptionDialog();
                }
                break;
            case R.id.ll3:
                selected_vv= 3;
                setLayoutSelected();
                if(isSwapOn && viewWhichTurnsOnSwap!=selected_vv){
                    if(!TextUtils.isEmpty(path_vv3))
                        performSwaping();
                }else {
                    hideOptions();
                    if (TextUtils.isEmpty(path_vv3) || path_vv3 == null)
                        chooseFile();
                    else
                        displayOptions();
//                    selectOptionDialog();
                }
                break;
            case R.id.ll4:
                selected_vv= 4;
                setLayoutSelected();
                if(isSwapOn && viewWhichTurnsOnSwap!=selected_vv){
                    if(!TextUtils.isEmpty(path_vv4))
                        performSwaping();
                }else {
                    hideOptions();
                    if (TextUtils.isEmpty(path_vv4) || path_vv4 == null)
                        chooseFile();
                    else
                        displayOptions();
//                    selectOptionDialog();
                }
                break;
        }
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    private boolean isOptionsToShow(int selected_vv) {
        if(selected_vv==1){
            return !TextUtils.isEmpty(path_vv1);
        }else if(selected_vv==2){
            return !TextUtils.isEmpty(path_vv2);
        }else if(selected_vv==3){
            return !TextUtils.isEmpty(path_vv3);
        }else if(selected_vv==4){
            return !TextUtils.isEmpty(path_vv4);
        }

        return false;
    }

//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        player.setDisplay(holder);
//        player.start();
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        //TODO: handle this
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        //TODO: handle this
//    }

    public void performSwaping(){
        tv_cancel.setVisibility(View.GONE);
        isSwapOn = false;
        if(viewWhichTurnsOnSwap==1){
            int temp_selected_vv= selected_vv;
            String tempPath = path_vv1;
            boolean tempMuteStatus = videosMuteStatus[0];
            long temp_video_time = timeInMillis_v1;
            if(selected_vv==2){
                path_vv1=path_vv2;
                path_vv2=tempPath;
                videosMuteStatus[0]=videosMuteStatus[2];
                videosMuteStatus[2]=tempMuteStatus;
                timeInMillis_v1 = timeInMillis_v2;
                timeInMillis_v2 = temp_video_time;
                selected_vv = 1;
                selectVVToLoadVideoInVideoView(path_vv1);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv2);
            }else if(selected_vv==3){
                path_vv1=path_vv3;
                path_vv3=tempPath;
                videosMuteStatus[0]=videosMuteStatus[2];
                videosMuteStatus[2]=tempMuteStatus;
                timeInMillis_v1 = timeInMillis_v3;
                timeInMillis_v3 = temp_video_time;
                selected_vv = 1;
                selectVVToLoadVideoInVideoView(path_vv1);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv3);
            }else if(selected_vv==4){
                path_vv1=path_vv4;
                path_vv4=tempPath;
                videosMuteStatus[0]=videosMuteStatus[3];
                videosMuteStatus[3]=tempMuteStatus;
                timeInMillis_v1 = timeInMillis_v4;
                timeInMillis_v4 = temp_video_time;
                selected_vv = 1;
                selectVVToLoadVideoInVideoView(path_vv1);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv4);
            }
        }else if(viewWhichTurnsOnSwap==2){
            int temp_selected_vv= selected_vv;
            String tempPath = path_vv2;
            boolean tempMuteStatus = videosMuteStatus[1];
            long temp_video_time = timeInMillis_v2;
            if(selected_vv==1){
                path_vv2=path_vv1;
                path_vv1=tempPath;
                videosMuteStatus[1]=videosMuteStatus[0];
                videosMuteStatus[0]=tempMuteStatus;
                timeInMillis_v2 = timeInMillis_v1;
                timeInMillis_v1 = temp_video_time;
                selected_vv = 2;
                selectVVToLoadVideoInVideoView(path_vv2);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv1);
            }else if(selected_vv==3){
                path_vv2=path_vv3;
                path_vv3=tempPath;
                videosMuteStatus[1]=videosMuteStatus[2];
                videosMuteStatus[2]=tempMuteStatus;
                timeInMillis_v2 = timeInMillis_v3;
                timeInMillis_v3 = temp_video_time;
                selected_vv = 2;
                selectVVToLoadVideoInVideoView(path_vv2);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv3);
            }else if(selected_vv==4){
                path_vv2=path_vv4;
                path_vv4=tempPath;
                videosMuteStatus[0]=videosMuteStatus[3];
                videosMuteStatus[3]=tempMuteStatus;
                timeInMillis_v2 = timeInMillis_v4;
                timeInMillis_v4 = temp_video_time;
                selected_vv = 2;
                selectVVToLoadVideoInVideoView(path_vv2);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv4);
            }
        }else if(viewWhichTurnsOnSwap==3){
            int temp_selected_vv= selected_vv;
            String tempPath = path_vv3;
            boolean tempMuteStatus = videosMuteStatus[2];
            long temp_video_time = timeInMillis_v3;
            if(selected_vv==1){
                path_vv3=path_vv1;
                path_vv1=tempPath;
                videosMuteStatus[2]=videosMuteStatus[0];
                videosMuteStatus[0]=tempMuteStatus;
                timeInMillis_v3 = timeInMillis_v1;
                timeInMillis_v1 = temp_video_time;
                selected_vv = 3;
                selectVVToLoadVideoInVideoView(path_vv3);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv1);
            }else if(selected_vv==2){
                path_vv3=path_vv2;
                path_vv2=tempPath;
                videosMuteStatus[2]=videosMuteStatus[1];
                videosMuteStatus[1]=tempMuteStatus;
                timeInMillis_v3 = timeInMillis_v2;
                timeInMillis_v2 = temp_video_time;
                selected_vv = 3;
                selectVVToLoadVideoInVideoView(path_vv3);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv2);
            }else if(selected_vv==4){
                path_vv3=path_vv4;
                path_vv4=tempPath;
                videosMuteStatus[2]=videosMuteStatus[3];
                videosMuteStatus[3]=tempMuteStatus;
                timeInMillis_v3 = timeInMillis_v4;
                timeInMillis_v4 = temp_video_time;
                selected_vv = 3;
                selectVVToLoadVideoInVideoView(path_vv3);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv4);
            }
        }else if(viewWhichTurnsOnSwap==4){
            int temp_selected_vv= selected_vv;
            String tempPath = path_vv4;
            boolean tempMuteStatus = videosMuteStatus[3];
            long temp_video_time = timeInMillis_v4;
            if(selected_vv==1){
                path_vv4=path_vv1;
                path_vv1=tempPath;
                videosMuteStatus[3]=videosMuteStatus[0];
                videosMuteStatus[0]=tempMuteStatus;
                timeInMillis_v4 = timeInMillis_v1;
                timeInMillis_v1 = temp_video_time;
                selected_vv = 4;
                selectVVToLoadVideoInVideoView(path_vv4);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv1);
            }else if(selected_vv==2){
                path_vv4=path_vv2;
                path_vv2=tempPath;
                videosMuteStatus[3]=videosMuteStatus[1];
                videosMuteStatus[1]=tempMuteStatus;
                timeInMillis_v4 = timeInMillis_v2;
                timeInMillis_v2 = temp_video_time;
                selected_vv = 4;
                selectVVToLoadVideoInVideoView(path_vv4);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv2);
            }else if(selected_vv==3){
                path_vv4=path_vv3;
                path_vv3=tempPath;
                videosMuteStatus[3]=videosMuteStatus[2];
                videosMuteStatus[2]=tempMuteStatus;
                timeInMillis_v4 = timeInMillis_v3;
                timeInMillis_v3 = temp_video_time;
                selected_vv = 4;
                selectVVToLoadVideoInVideoView(path_vv4);
                selected_vv = temp_selected_vv;
                selectVVToLoadVideoInVideoView(path_vv3);
            }
        }
        setMuteStatus();
        displayOptions();
    }
    public void swapVideos(){
        isSwapOn=true;
        ll_options.setVisibility(View.INVISIBLE);
        tv_cancel.setVisibility(View.VISIBLE);
        viewWhichTurnsOnSwap = selected_vv;
    }
    private void chooseFile() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("*/*");
            try {
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);

            } catch (ActivityNotFoundException e) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            filePath="";
            if (requestCode == PICK_FILE_REQUEST_CODE) {
                try {
                    displayOptions();
//                    videosMuteStatus = new boolean[]{false, false, false, false};
                    System.out.println(":::File URI:::"+data.getData());
                    String file_uri = data.getData().toString();
                    if(file_uri.contains("fileprovider"))
                        filePath = file_uri.substring(file_uri.indexOf("/storage"));
                    else
                        filePath = GetFilePathFromDevice.getPath(MainActivity.this, data.getData());
                    Log.e("FILE PATH", "---" + filePath);
                    if(selected_vv==1)
                        isToAddWatermark1 = true;
                    else if(selected_vv==2)
                        isToAddWatermark2 = true;
                    else if(selected_vv==3)
                        isToAddWatermark3 = true;
                    else if(selected_vv==4)
                        isToAddWatermark4 = true;

                    selectVVToLoadVideoInVideoView(filePath);

                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    myFile = new File(filePath);
                    Log.e("File", "---" + myFile);
                    Log.e("File SIZE", "---" + Integer.parseInt(String.valueOf(myFile.length() / 1024)));
                    fileSize = Integer.parseInt(String.valueOf(myFile.length() / 1024));
                    if (fileSize > 102400) {
                        Toast.makeText(MainActivity.this, "Please select file less than 100MB.", Toast.LENGTH_SHORT).show();
                    } else {

                        if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                        }
                        Log.e("display name ", ">>" + displayName);
                        //strImage = convertFileToByteArray(myFile);

                        //Log.e("Base64", "---" + strImage);
                        detectFileExtension(filePath, displayName, fileSize);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void detectFileExtension(String path, String fileNAme, int fileSize){
        System.out.println("path: "+path);
        System.out.println("fileNAme: "+fileNAme);
        String[] extensionArr = new String[]{"mp4", "3gp"};
        List<String> extensionList = new ArrayList<>(Arrays.asList(extensionArr));
        String extension = path.substring(path.lastIndexOf("."));
        extension_splited = extension.substring(1);

        String[] fileNameArr = path.split("/");

        if(extensionList.contains(extension_splited)) {
          /*  isToSetResolution = true;
            selectVVToLoadVideoInVideoView(filePath);*/
            System.out.println("added file extension: " + extension);
            attached_fileSize = fileSize;
            attached_fileName = fileNameArr[fileNameArr.length-1];
//            base64file = GetFilePathFromDevice.convertFileToByteArray(myFile);
        }
        else{
            Toast.makeText(MainActivity.this, "Sorry! You can't load this type of file", Toast.LENGTH_SHORT).show();
        }

    }

    public void selectVVToLoadVideoInVideoView(String filesPath){
        try {
            switch (selected_vv){
                case 1:
                    path_vv1 = filesPath;
                    loadVideo1(path_vv1);
                    timeInMillis_v1 = videoTime(path_vv1);
                    if(isToAddWatermark1) {
                        executeWaterMarkCommandVideo1(filesPath);
                        isToAddWatermark1=false;
                    }
                    break;
                case 2:
                    path_vv2 = filesPath;
                    loadVideo2(path_vv2);
                    timeInMillis_v2 = videoTime(path_vv2);
                    if(isToAddWatermark2) {
                        executeWaterMarkCommandVideo2(filesPath);
                        isToAddWatermark2=false;
                    }
                    break;
                case 3:
                    path_vv3 = filesPath;
                    loadVideo3(path_vv3);
                    timeInMillis_v3 = videoTime(path_vv3);
                    if(isToAddWatermark3) {
                        executeWaterMarkCommandVideo3(filesPath);
                        isToAddWatermark3=false;
                    }
                    break;
                case 4:
                    path_vv4 = filesPath;
                    loadVideo4(path_vv4);
                    timeInMillis_v4 = videoTime(path_vv4);
                    if(isToAddWatermark4) {
                        executeWaterMarkCommandVideo4(filesPath);
                        isToAddWatermark4=false;
                    }
                    break;
            }
            if(isToSetResolution) {
//                executeWaterMarkCommand(filesPath);
                //executeMaintainAspectRatioCommand(filesPath);
                isToSetResolution=false;
            }
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

//    public void loadVideologo(String path){
//        vv_logo.setVideoURI(Uri.parse(path));
//
//        vv_logo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//            }
//        });
//        vv_logo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
////                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
//                return false;
//            }
//        });
//
//        vv_logo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//                System.out.println("setOnPreparedListener:::");
//                vv_logo.start();
//            }
//        });
//
//    }
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
    public void loadVideo1(String path){
        Uri uri=Uri.parse(path);
        vv1.setVideoURI(uri);

        vv1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        vv1.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        vv1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                System.out.println("setOnPreparedListener:::");
                vv1.start();
                mp1 = mp;
            }
        });

    }
    public void loadVideo2(String path){

        vv2.setVideoURI(Uri.fromFile(new File(path)));

        vv2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        vv2.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        vv2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                System.out.println("setOnPreparedListener:::");
                vv2.start();
                mp2 = mp;
            }
        });

    }
    public void loadVideo3(String path){

        vv3.setVideoURI(Uri.fromFile(new File(path)));

        vv3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        vv3.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        vv3.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                System.out.println("setOnPreparedListener:::");
                vv3.start();
                mp3 = mp;
            }
        });

    }
    public void loadVideo4(String path){

        vv4.setVideoURI(Uri.fromFile(new File(path)));

        vv4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        vv4.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        vv4.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                System.out.println("setOnPreparedListener:::");
                vv4.start();
                mp4 = mp;
            }
        });

    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        System.out.println("onConfigurationChanged");
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    }

    private class BackMediaController extends MediaController {
//        private VideoPlayer player;

        public BackMediaController(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BackMediaController(Context context) {
            super(context);
//            this.player = player;
        }

        @Override
        public void setAnchorView(View view) {
            super.setAnchorView(view);
//            if(userRole==3){
//                final ImageView delBtn = new ImageView(getContext());
//                delBtn.setImageResource(R.drawable.ic_delete_forever);
//                float padding = 20f;
//                delBtn.setPadding((int) padding+20, (int) padding, (int) padding, (int) padding);
//                FrameLayout.LayoutParams paramsDel = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//                paramsDel.gravity = Gravity.START;
//                addView(delBtn, paramsDel);
//                delBtn.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        deleteAttachedImg();
//                    }
//                });
//            }
//
//
//            float padding = 20f;
//            final ImageView enlargeView = new ImageView(getContext());
//            enlargeView.setImageResource(R.drawable.ic_media_fullscreen_shrink);
//            enlargeView.setPadding((int) padding, (int) padding, (int) padding+20, (int) padding);
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//            params.gravity = Gravity.END;
//            addView(enlargeView, params);
//
//            enlargeView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(isExpanded) {
//                        isExpanded=false;
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        enlargeView.setImageResource(R.drawable.ic_media_fullscreen_shrink);
//                    }else{
//                        isExpanded=true;
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                        enlargeView.setImageResource(R.drawable.ic_media_fullscreen_stretch);
//                    }
//                }
//            });
        }
    }

    public BackMediaController getMediaControllers(){
        if(selected_vv==1)
            return mControl1;
        else if(selected_vv==2)
            return mControl2;
        else if(selected_vv==3)
            return mControl3;
        else
            return mControl4;
    }

    public void setLayoutSelected(){
        if(selected_vv==1){
            ll1.setBackgroundResource(R.drawable.home_selection);
            ll2.setBackgroundResource(R.drawable.main_lay_background);
            ll3.setBackgroundResource(R.drawable.main_lay_background);
            ll4.setBackgroundResource(R.drawable.main_lay_background);
            if(!videosMuteStatus[0])
                iv_mute.setImageResource(R.drawable.ic_volume_on);
//                tv_mute.setText("Mute This Video");
            else
                iv_mute.setImageResource(R.drawable.ic_volume_off);
//                tv_mute.setText("Un mute This Video");
        }else if(selected_vv==2){
            ll1.setBackgroundResource(R.drawable.main_lay_background);
            ll2.setBackgroundResource(R.drawable.home_selection);
            ll3.setBackgroundResource(R.drawable.main_lay_background);
            ll4.setBackgroundResource(R.drawable.main_lay_background);
            if(!videosMuteStatus[1])
                iv_mute.setImageResource(R.drawable.ic_volume_on);
//                tv_mute.setText("Mute This Video");
            else
                iv_mute.setImageResource(R.drawable.ic_volume_off);
//                tv_mute.setText("Un mute This Video");
        }else if(selected_vv==3){
            ll1.setBackgroundResource(R.drawable.main_lay_background);
            ll2.setBackgroundResource(R.drawable.main_lay_background);
            ll3.setBackgroundResource(R.drawable.home_selection);
            ll4.setBackgroundResource(R.drawable.main_lay_background);
            if(!videosMuteStatus[2])
                iv_mute.setImageResource(R.drawable.ic_volume_on);
//                tv_mute.setText("Mute This Video");
            else
                iv_mute.setImageResource(R.drawable.ic_volume_off);
//                tv_mute.setText("Un mute This Video");
        }else if(selected_vv==4){
            ll1.setBackgroundResource(R.drawable.main_lay_background);
            ll2.setBackgroundResource(R.drawable.main_lay_background);
            ll3.setBackgroundResource(R.drawable.main_lay_background);
            ll4.setBackgroundResource(R.drawable.home_selection);
            if(!videosMuteStatus[3])
                iv_mute.setImageResource(R.drawable.ic_volume_on);
//                tv_mute.setText("Mute This Video");
            else
                iv_mute.setImageResource(R.drawable.ic_volume_off);
//                tv_mute.setText("Un mute This Video");
        }
    }

    public void setMuteStatus(){
        if(selected_vv==1){
            if(!videosMuteStatus[0])
                iv_mute.setImageResource(R.drawable.ic_volume_on);
//                tv_mute.setText("Mute This Video");
            else
                iv_mute.setImageResource(R.drawable.ic_volume_off);
//                tv_mute.setText("Un mute This Video");
        }else if(selected_vv==2){
            if(!videosMuteStatus[1])
                iv_mute.setImageResource(R.drawable.ic_volume_on);
//                tv_mute.setText("Mute This Video");
            else
                iv_mute.setImageResource(R.drawable.ic_volume_off);
//                tv_mute.setText("Un mute This Video");
        }else if(selected_vv==3){
            if(!videosMuteStatus[2])
                iv_mute.setImageResource(R.drawable.ic_volume_on);
//                tv_mute.setText("Mute This Video");
            else
                iv_mute.setImageResource(R.drawable.ic_volume_off);
//                tv_mute.setText("Un mute This Video");
        }else if(selected_vv==4){
            if(!videosMuteStatus[3])
                iv_mute.setImageResource(R.drawable.ic_volume_on);
//                tv_mute.setText("Mute This Video");
            else
                iv_mute.setImageResource(R.drawable.ic_volume_off);
//                tv_mute.setText("Un mute This Video");
        }
    }

    public void goToTrimActivity(String path, long videoDurationInMillis){
        Intent intent = new Intent(this, TrimmerActivity.class);
        intent.putExtra(HelperClass.EXTRA_VIDEO_PATH, path);
        intent.putExtra(HelperClass.VIDEO_TOTAL_DURATION, (int)videoDurationInMillis);
        startActivity(intent);
    }
    public void selectOptionDialog()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
//        mBuilder.setCancelable(true);
        final View mView = getLayoutInflater().inflate(R.layout.custom_video_options_layout, null);

        Button btn_cancel = mView.findViewById(R.id.btn_cancel);
        TextView tv_reload = mView.findViewById(R.id.tv_reload);
        TextView tv_trim = mView.findViewById(R.id.tv_trim);
        TextView tv_mute = mView.findViewById(R.id.tv_mute);

        if(selected_vv==1){
            if(!videosMuteStatus[0])
                tv_mute.setText("Mute This Video");
            else
                tv_mute.setText("Un mute This Video");
        }else if(selected_vv==2){
            if(!videosMuteStatus[1])
                tv_mute.setText("Mute This Video");
            else
                tv_mute.setText("Un mute This Video");
        }else if(selected_vv==3){
            if(!videosMuteStatus[2])
                tv_mute.setText("Mute This Video");
            else
                tv_mute.setText("Un mute This Video");
        }else if(selected_vv==4){
            if(!videosMuteStatus[3])
                tv_mute.setText("Mute This Video");
            else
                tv_mute.setText("Un mute This Video");
        }


        mBuilder.setView(mView);
        final Dialog dialog = new Dialog(MainActivity.this, R.style.NewDialog);
        dialog.setContentView(mView);
//        dialog.setCancelable(false);
        dialog.show();

        tv_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_vv==1){
                        if(!videosMuteStatus[0]) {
                            mute(mp1);
                            videosMuteStatus[0] = true;
                        } else {
                            unmute(mp1);
                            videosMuteStatus[0] = false;
                        }
                }else if(selected_vv==2){
                    if(!videosMuteStatus[1]) {
                        mute(mp2);
                        videosMuteStatus[1] = true;
                    } else {
                        unmute(mp2);
                        videosMuteStatus[1] = false;
                    }
                }else if(selected_vv==3){
                    if(!videosMuteStatus[2]) {
                        mute(mp3);
                        videosMuteStatus[2] = true;
                    } else {
                        unmute(mp3);
                        videosMuteStatus[2] = false;
                    }
                }else if(selected_vv==4){
                    if(!videosMuteStatus[3]) {
                        mute(mp4);
                        videosMuteStatus[3] = true;
                    } else {
                        unmute(mp4);
                        videosMuteStatus[3] = false;
                    }
                }
                dialog.dismiss();
            }
        });
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
                dialog.dismiss();
            }
        });
        tv_trim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_vv==1)
                    trimVideoDialog(timeInMillis_v1, path_vv1);
                else if(selected_vv==2)
                    trimVideoDialog(timeInMillis_v2, path_vv2);
                else if(selected_vv==3)
                    trimVideoDialog(timeInMillis_v3, path_vv3);
                else if(selected_vv==4)
                    trimVideoDialog(timeInMillis_v4, path_vv4);
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    int startTime = -1;
    int endTime = -1;

    int preMin = -1;
    int preMax = -1;
    public void trimVideoDialog(final long videoMillis, final String videoPath)
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
//        mBuilder.setCancelable(true);
        final View mView = getLayoutInflater().inflate(R.layout.custom_video_trim_layout, null);

        Button btn_trim = mView.findViewById(R.id.btn_trim);
        Button btn_cancel = mView.findViewById(R.id.btn_cancel);
        final TextView tv_start_time = mView.findViewById(R.id.tv_start_time);
        final TextView tv_end_time = mView.findViewById(R.id.tv_end_time);

        tv_start_time.setText(convertMillisToTime(0));
        tv_end_time.setText(convertMillisToTime(videoMillis));
        startTime = 0;
        endTime = (int) videoMillis;
        preMin = -1;
        preMax = -1;

        final RangeSeekBar<Integer> seekBar = mView.findViewById(R.id.rangeSeekbar);
        seekBar.setRangeValues(0, (int) videoMillis);
//setNotifyWhileDragging is important method to achive this functionality
        seekBar.setNotifyWhileDragging(true);

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                int diff = maxValue - minValue;
                if (diff < 1) {
                    bar.setEnabled(false);
                    if(minValue != preMin){
                        seekBar.setSelectedMinValue(preMin);
                    }
                    else if(maxValue != preMax){
                        seekBar.setSelectedMaxValue(preMax);
                    }
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setNegativeButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            seekBar.setEnabled(true);
                        }
                    });
                    alert.setCancelable(false);
                    alert.setMessage(Html.fromHtml("Your video should be at least one sec long!!")).show();

                } else {
                    startTime = minValue;
                    endTime = maxValue;
                    tv_start_time.setText(convertMillisToTime(minValue));
                    tv_end_time.setText(convertMillisToTime(maxValue));

                    preMin = minValue;
                    preMax = maxValue;
                }
            }
        });



        mBuilder.setView(mView);
        final Dialog dialog = new Dialog(MainActivity.this, R.style.NewDialog);
        dialog.setContentView(mView);
//        dialog.setCancelable(false);
        dialog.show();
        btn_trim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeMillis = String.valueOf(new Date().getTime());
                String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                mFileName += "/MergedVideos/"+timeMillis+".mp4";
                try {
                    String[] cutVideo = {"-ss", "" + startTime / 1000, "-y", "-i", videoPath, "-t", "" + (videoMillis - startTime-(videoMillis-endTime)) / 1000, "-s", "320x240", "-r", "15", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", mFileName};
                    executeTrimCommand(cutVideo, mFileName);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public long videoTime(String path){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this, Uri.fromFile(new File(path)));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );
        retriever.release();

        return timeInMillisec;
    }

    public String convertMillisToTime(long milliseconds){
        String seconds = String.format("%02d", (int) (milliseconds / 1000) % 60) ;
        String minutes = String.format("%02d", (int) (milliseconds / (1000*60)) % 60);
        String hours   = String.format("%02d", (int) (milliseconds / (1000*60*60)) % 24);

        return hours+":"+minutes+":"+seconds;
    }

    public void mute(MediaPlayer mediaPlayer) {
        iv_mute.setImageResource(R.drawable.ic_volume_off);
        setVolume(0, mediaPlayer);
    }

    public void unmute(MediaPlayer mediaPlayer) {
        iv_mute.setImageResource(R.drawable.ic_volume_on);
        setVolume(100, mediaPlayer);
    }

    private void setVolume(int amount, MediaPlayer mediaPlayer) {
        final int max = 100;
        final double numerator = max - amount > 0 ? Math.log(max - amount) : 0;
        final float volume = (float) (1 - (numerator / Math.log(max)));

//        if (mp1.isPlaying()) {
//            mp1.stop();
//            mp1.release();
//            mp1 = new MediaPlayer();
//        }

        if(selected_vv==1) {
            mp1.setVolume(volume, volume);
        }else if(selected_vv==2) {
            mp2.setVolume(volume, volume);
        }else if(selected_vv==3) {
            mp3.setVolume(volume, volume);
        }else if(selected_vv==4) {
            mp4.setVolume(volume, volume);
        }


//        mp1.setLooping(false);
//        mp1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mp1.start();
//            }
//        });

    }

    public boolean isVideosCollageable(){
        int counter=0;
        if(!TextUtils.isEmpty(path_vv1))
            ++counter;
        if(!TextUtils.isEmpty(path_vv2))
            ++counter;
        if(!TextUtils.isEmpty(path_vv3))
            ++counter;
        if(!TextUtils.isEmpty(path_vv4))
            ++counter;

        return counter == 4;
    }

    public void loadFFmpegLib() throws FFmpegNotSupportedException {
        if(fFmpeg==null){
            fFmpeg = FFmpeg.getInstance(MainActivity.this);

            fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
//                    Toast.makeText(MainActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
//                    String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
//                    mFileName += "/MergedVideos/wishbyvideo.mp4";
//                    Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
////                    String[] command = new String[]{"ffmpeg-y","-i","/storage/emulated/0/DCIM/Camera/VID_20191217_132801.mp4"};
////                    String str_command = "-i /storage/emulated/0/DCIM/Camera/VID_20191217_132801.mp4 -vcodec h264 -acodec mp2 /storage/emulated/0/DCIM/Camera/VID_20191217_111111.mp4";
//
////                    String str_command = "ffmpeg -i /storage/emulated/0/DCIM/Camera/VID_20191224_143402.mp4 -i /storage/emulated/0/Mobizen/mobizen_20191218_115828.mp4 -filter_complex '[0:v]pad=iw*2:ih[int];[int][1:v]overlay=W/2:0[vid]' -map [vid] -c:v libx264 -crf 23 -preset veryfast "+mFileName;
//                    String str_command = "ffmpeg -i /storage/emulated/0/DCIM/Camera/VID_20191224_143402.mp4 -i /storage/emulated/0/Mobizen/mobizen_20191218_115828.mp4 \\-filter_complex \\\"[0:v]pad=iw*2:ih[int]; \\[int][1:v]overlay=W/2:0[vid]\" \\-map \"[vid]\" \\-c:v libx264 -crf 23 \\"+mFileName;
//                    String[] command = str_command.split(" ");
//                    try {
////                        String[] command_array_compress_video = {"-y", "-i", "/storage/emulated/0/DCIM/Camera/VID_20191224_143402.mp4", "-s", "160x120", "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", mFileName};
//                        String[] cutVideo = {"-ss", "" + 60000 / 1000, "-y", "-i", "/storage/emulated/0/Mobizen/mobizen_20191218_115828.mp4", "-t", "" + (228000 - 120000) / 1000, "-s", "320x240", "-r", "15", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", mFileName};
//                        executeCommand(cutVideo);
//                    } catch (FFmpegCommandAlreadyRunningException e) {
//                        e.printStackTrace();
//                    }
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        }
    }

    public void executeTrimCommand(String[] command, final String filePath) throws FFmpegCommandAlreadyRunningException {

        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        ////////
        createDirectory();
//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//        File myDir = new File(root + "/MergedVideos");
//        if(!myDir.exists())
//            myDir.mkdirs();

//        File file = new File (myDir, "wishbyvideo.mp4");
//        if (file.exists ())
//            file.delete ();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
////            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //////////////////
        fFmpeg.execute(command, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Video trimmed successfully!", Toast.LENGTH_SHORT).show();
                selectVVToLoadVideoInVideoView(filePath);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
//                selectVVToLoadVideoInVideoView();
                System.out.println("command:onFinish:");
            }
        });
    }
    public void executeWaterMarkCommandVideo1(final String preFilePath) throws FFmpegCommandAlreadyRunningException {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage("Loading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);//initially progress is 0
        progressBar.setMax(100);//sets the maximum value 100
        progressBar.show();//displays the progress bar
        // Set Progress
// or with animation
        //circularProgressBar.setProgressWithAnimation(0.0f, (long) 1000); // =1s

// Set Progress Max
       // circularProgressBar.setVisibility(View.VISIBLE);
        //circularProgressBar.setProgressMax(100f);
//        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
        String logo = Environment.getExternalStorageDirectory().getAbsolutePath() +"/num"+(selected_vv)+".png";
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";
        System.out.println("::logo::"+logo);
        // this is meant for a fixed size water mark replace W/H with wifht and height
        //[1:v][0:v]scale2ref=(W/H)*ih/8/sar:ih/8[wm][base];[base][wm]
//        String[] cutVideo = {"-ss", "" + startTime / 1000, "-y", "-i", videoPath, "-t", "" + (videoMillis - startTime-(videoMillis-endTime)) / 1000, "-s", "320x240", "-r", "15", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", mFileName};
//        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=10:main_h-overlay_h-10", mFileName};
          //"overlay=(main_w-overlay_w)/2:main_h-overlay_h"
        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=(main_w-overlay_w)/2:main_h-overlay_h","-vcodec", "h264", "-b:v", "2097152","-b:a", "48000","-c:v", "libx264",  "-preset", "ultrafast","-c:a", "copy","-me_method","zero","-tune","fastdecode","-tune","zerolatency","-strict","2","-pix_fmt","yuv420p", "-crf", "28", "-acodec", "aac", "-ar", "22050", "-ac", "2","-r","20", mFileName};
//        "ffmpeg -i input.mp4 -i logo.png -filter_complex \\\n" +
//                "\"overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2\" \\\n" +
//                "-codec:a copy output.mp4"


//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//        File myDir = new File(root + "/MergedVideos");
//        if(!myDir.exists())
//            myDir.mkdirs();

        mProgressCalculator = new ProgressCalculator();
        final String finalMFileName = mFileName;
        fFmpeg.execute(addWatermark, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                status = SUCCESS;
                progressBar.dismiss();
//                Toast.makeText(MainActivity.this, "Water mark added!", Toast.LENGTH_SHORT).show();
                selectVVToLoadVideoInVideoView(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                status = RUNNING;
                Log.e("VideoCronProgress", message);
                int progress = mProgressCalculator.calcProgress(message);
                if (progress != 0 && progress <= 100) {
                    if (progress >= 99) {
                        progressBar.setMessage("Almost done!");
                        progress = 100;
                    }
                    progressBar.setProgress(progress);
                    //circularProgressBar.setProgress(progress);
                    System.out.println("pro_progress"+progress+"%%");
//                    listener.onProgress(progress);
                }
                if(progress==50){
                    progressBar.setMessage("Compressing Video, Please wait!!");
                }
                if(progress==100){
                    Log.e("i am ","here");
                    progressBar.setProgress(100);
                    //circularProgressBar.setProgress(100f);
                }
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
                status = FAILED;
                //progressBar.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
                //circularProgressBar.setProgress(0.0f);
                //circularProgressBar.refreshDrawableState();
                //circularProgressBar.setVisibility(View.GONE);
                progressBar.dismiss();
                iv_mute_button1.setVisibility(View.VISIBLE);
//                selectVVToLoadVideoInVideoView();
                System.out.println("command:onFinish:");
            }
        });
    }

    //ffmpeg 2
    public void executeWaterMarkCommandVideo2(final String preFilePath) throws FFmpegCommandAlreadyRunningException {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage("Loading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);//initially progress is 0
        progressBar.setMax(100);//sets the maximum value 100
        progressBar.show();//displays the progress bar
        // Set Progress
// or with animation
        //circularProgressBar.setProgressWithAnimation(0.0f, (long) 1000); // =1s

// Set Progress Max
        // circularProgressBar.setVisibility(View.VISIBLE);
        //circularProgressBar.setProgressMax(100f);
//        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
        String logo = Environment.getExternalStorageDirectory().getAbsolutePath() +"/num"+(selected_vv)+".png";
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";
        System.out.println("::logo::"+logo);
//        String[] cutVideo = {"-ss", "" + startTime / 1000, "-y", "-i", videoPath, "-t", "" + (videoMillis - startTime-(videoMillis-endTime)) / 1000, "-s", "320x240", "-r", "15", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", mFileName};
//        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=10:main_h-overlay_h-10", mFileName};
        //"overlay=(main_w-overlay_w)/2:main_h-overlay_h"
        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=(main_w-overlay_w)/2:main_h-overlay_h","-vcodec", "h264", "-b:v", "2097152","-b:a", "48000","-c:v", "libx264",  "-preset", "ultrafast","-c:a", "copy","-me_method","zero","-tune","fastdecode","-tune","zerolatency","-strict","2","-pix_fmt","yuv420p", "-crf", "28", "-acodec", "aac", "-ar", "22050", "-ac", "2","-r","20", mFileName};
//        "ffmpeg -i input.mp4 -i logo.png -filter_complex \\\n" +
//                "\"overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2\" \\\n" +
//                "-codec:a copy output.mp4"


//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//        File myDir = new File(root + "/MergedVideos");
//        if(!myDir.exists())
//            myDir.mkdirs();

        mProgressCalculator = new ProgressCalculator();
        final String finalMFileName = mFileName;
        fFmpeg.execute(addWatermark, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                status = SUCCESS;
                progressBar.dismiss();
//                Toast.makeText(MainActivity.this, "Water mark added!", Toast.LENGTH_SHORT).show();
                selectVVToLoadVideoInVideoView(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                status = RUNNING;
                Log.e("VideoCronProgress", message);
                int progress = mProgressCalculator.calcProgress(message);
                if (progress != 0 && progress <= 100) {
                    if (progress >= 99) {
                        progressBar.setMessage("Almost done!");
                        progress = 100;
                    }
                    progressBar.setProgress(progress);
                    //circularProgressBar.setProgress(progress);
                    System.out.println("pro_progress"+progress+"%%");
//                    listener.onProgress(progress);
                }
                if(progress==50){
                    progressBar.setMessage("Compressing Video, Please wait!!");
                }
                if(progress==100){
                    Log.e("i am ","here");
                    progressBar.setProgress(100);
                    //circularProgressBar.setProgress(100f);
                }
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
                status = FAILED;
                //progressBar.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
                //circularProgressBar.setProgress(0.0f);
                //circularProgressBar.refreshDrawableState();
                //circularProgressBar.setVisibility(View.GONE);
                progressBar.dismiss();
                iv_mute_button2.setVisibility(View.VISIBLE);
//                selectVVToLoadVideoInVideoView();
                System.out.println("command:onFinish:");
            }
        });
    }
    // ffmpeg 3
    public void executeWaterMarkCommandVideo3(final String preFilePath) throws FFmpegCommandAlreadyRunningException {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage("Loading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);//initially progress is 0
        progressBar.setMax(100);//sets the maximum value 100
        progressBar.show();//displays the progress bar
        // Set Progress
// or with animation
        //circularProgressBar.setProgressWithAnimation(0.0f, (long) 1000); // =1s

// Set Progress Max
        // circularProgressBar.setVisibility(View.VISIBLE);
        //circularProgressBar.setProgressMax(100f);
//        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
        String logo = Environment.getExternalStorageDirectory().getAbsolutePath() +"/num"+(selected_vv)+".png";
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";
        System.out.println("::logo::"+logo);
//        String[] cutVideo = {"-ss", "" + startTime / 1000, "-y", "-i", videoPath, "-t", "" + (videoMillis - startTime-(videoMillis-endTime)) / 1000, "-s", "320x240", "-r", "15", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", mFileName};
//        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=10:main_h-overlay_h-10", mFileName};
        //"overlay=(main_w-overlay_w)/2:main_h-overlay_h"
        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=(main_w-overlay_w)/2:main_h-overlay_h","-vcodec", "h264", "-b:v", "2097152","-b:a", "48000","-c:v", "libx264",  "-preset", "ultrafast","-c:a", "copy","-me_method","zero","-tune","fastdecode","-tune","zerolatency","-strict","2","-pix_fmt","yuv420p", "-crf", "28", "-acodec", "aac", "-ar", "22050", "-ac", "2","-r","20", mFileName};
//        "ffmpeg -i input.mp4 -i logo.png -filter_complex \\\n" +
//                "\"overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2\" \\\n" +
//                "-codec:a copy output.mp4"


//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//        File myDir = new File(root + "/MergedVideos");
//        if(!myDir.exists())
//            myDir.mkdirs();

        mProgressCalculator = new ProgressCalculator();
        final String finalMFileName = mFileName;
        fFmpeg.execute(addWatermark, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                status = SUCCESS;
                progressBar.dismiss();
//                Toast.makeText(MainActivity.this, "Water mark added!", Toast.LENGTH_SHORT).show();
                selectVVToLoadVideoInVideoView(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                status = RUNNING;
                Log.e("VideoCronProgress", message);
                int progress = mProgressCalculator.calcProgress(message);
                if (progress != 0 && progress <= 100) {
                    if (progress >= 99) {
                        progressBar.setMessage("Almost done!");
                        progress = 100;
                    }
                    progressBar.setProgress(progress);
                    //circularProgressBar.setProgress(progress);
                    System.out.println("pro_progress"+progress+"%%");
//                    listener.onProgress(progress);
                }
                if(progress==50){
                    progressBar.setMessage("Compressing Video, Please wait!!");
                }
                if(progress==100){
                    Log.e("i am ","here");
                    progressBar.setProgress(100);
                    //circularProgressBar.setProgress(100f);
                }
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
                status = FAILED;
                //progressBar.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
                //circularProgressBar.setProgress(0.0f);
                //circularProgressBar.refreshDrawableState();
                //circularProgressBar.setVisibility(View.GONE);
                progressBar.dismiss();
                iv_mute_button3.setVisibility(View.VISIBLE);
//                selectVVToLoadVideoInVideoView();
                System.out.println("command:onFinish:");
            }
        });
    }
    //ffmpeg 4
    public void executeWaterMarkCommandVideo4(final String preFilePath) throws FFmpegCommandAlreadyRunningException {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage("Loading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);//initially progress is 0
        progressBar.setMax(100);//sets the maximum value 100
        progressBar.show();//displays the progress bar
        // Set Progress
// or with animation
        //circularProgressBar.setProgressWithAnimation(0.0f, (long) 1000); // =1s

// Set Progress Max
        // circularProgressBar.setVisibility(View.VISIBLE);
        //circularProgressBar.setProgressMax(100f);
//        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
        String logo = Environment.getExternalStorageDirectory().getAbsolutePath() +"/num"+(selected_vv)+".png";
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";
        System.out.println("::logo::"+logo);
//        String[] cutVideo = {"-ss", "" + startTime / 1000, "-y", "-i", videoPath, "-t", "" + (videoMillis - startTime-(videoMillis-endTime)) / 1000, "-s", "320x240", "-r", "15", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", mFileName};
//        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=10:main_h-overlay_h-10", mFileName};
        //"overlay=(main_w-overlay_w)/2:main_h-overlay_h"
        //[0:v][1:v] uncomment if required
        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=(main_w-overlay_w)/2:main_h-overlay_h","-vcodec", "h264", "-b:v", "2097152","-b:a", "48000","-c:v", "libx264",  "-preset", "ultrafast","-c:a", "copy","-me_method","zero","-tune","fastdecode","-tune","zerolatency","-strict","2","-pix_fmt","yuv420p", "-crf", "28", "-acodec", "aac", "-ar", "22050", "-ac", "2","-r","20", mFileName};
//        "ffmpeg -i input.mp4 -i logo.png -filter_complex \\\n" +
//                "\"overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2\" \\\n" +
//                "-codec:a copy output.mp4"


//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//        File myDir = new File(root + "/MergedVideos");
//        if(!myDir.exists())
//            myDir.mkdirs();

        mProgressCalculator = new ProgressCalculator();
        final String finalMFileName = mFileName;
        fFmpeg.execute(addWatermark, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                status = SUCCESS;
                progressBar.dismiss();
//                Toast.makeText(MainActivity.this, "Water mark added!", Toast.LENGTH_SHORT).show();
                selectVVToLoadVideoInVideoView(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                status = RUNNING;
                Log.e("VideoCronProgress", message);
                int progress = mProgressCalculator.calcProgress(message);
                if (progress != 0 && progress <= 100) {
                    if (progress >= 99) {
                        progressBar.setMessage("Almost done!");
                        progress = 100;
                    }
                    progressBar.setProgress(progress);
                    //circularProgressBar.setProgress(progress);
                    System.out.println("pro_progress"+progress+"%%");
//                    listener.onProgress(progress);
                }
                if(progress==50){
                    progressBar.setMessage("Compressing Video, Please wait!!");
                }
                if(progress==100){
                    Log.e("i am ","here");
                    progressBar.setProgress(100);
                    //circularProgressBar.setProgress(100f);
                }
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
                status = FAILED;
                //progressBar.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
                //circularProgressBar.setProgress(0.0f);
                //circularProgressBar.refreshDrawableState();
                //circularProgressBar.setVisibility(View.GONE);
                progressBar.dismiss();
                iv_mute_button4.setVisibility(View.VISIBLE);
//                selectVVToLoadVideoInVideoView();
                System.out.println("command:onFinish:");
            }
        });
    }
    public void executeMaintainAspectRatioCommand(final String preFilePath) throws FFmpegCommandAlreadyRunningException {

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage("Loading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);//initially progress is 0
        progressBar.setMax(100);//sets the maximum value 100
        progressBar.show();//displays the progress bar

//      progressDialogResWatMark = ProgressDialog.show(this, "", "Loading...", true);
        createDirectory();
        String logo = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/num"+(selected_vv)+".png";
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";
        System.out.println("::logo::"+logo);
//        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=10:main_h-overlay_h-10", mFileName};
        String[] aspectRatioSelected=new String[]{};
        if(resolution_type==1) {
            //aspectRatioSelected = new String[]{"-i", preFilePath,"-vcodec", "h264", "-b:v", "1000k","-b:a", "48000","-c:v", "libx264",  "-preset", "ultrafast","-c:a", "copy","-me_method","zero","-tune","fastdecode","-tune","zerolatency","-strict","2","-pix_fmt","yuv420p", "-crf", "28", "-acodec", "aac", "-ar", "22050", "-ac", "2", "-s", "240x320","-r","20", "-aspect", "3:4", mFileName};
            aspectRatioSelected = new String[]{"-i", preFilePath, "-vf", "scale=240x320,setdar=3:4", mFileName};
        }else {
            //aspectRatioSelected = new String[]{"-y","-i", preFilePath,"-vcodec", "h264", "-b:v", "1000k","-b:a", "48000","-c:v", "libx264",  "-preset", "ultrafast","-c:a", "copy","-me_method","zero","-tune","fastdecode","-tune","zerolatency","-strict","2", "-pix_fmt","yuv420p","-crf", "28", "-acodec", "aac", "-ar", "22050", "-ac", "2", "-s", "406x720","-r","20", "-aspect", "9:16", mFileName};
            aspectRatioSelected = new String[]{"-i", preFilePath, "-vf", "scale=406x720,setdar=9:16", mFileName};
        }

        mProgressCalculator = new ProgressCalculator();

        final String finalMFileName = mFileName;
        fFmpeg.execute(aspectRatioSelected, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
//                progressDialog.dismiss();
//                Toast.makeText(MainActivity.this, "Aspect Ratio added!", Toast.LENGTH_SHORT).show();
                //isToAddWatermark = true;
                selectVVToLoadVideoInVideoView(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = mProgressCalculator.calcProgress(message);
                        Log.e("VideoCronProgress == ", progress + "..");
                        if (progress != 0 && progress <= 100) {
                            if (progress >= 99) {
                                progressBar.setMessage("Almost done!");
                                progress = 100;
                            }
                            progressBar.setProgress(progress);
                            System.out.println("pro_progress"+progress+"%%");
//                    listener.onProgress(progress);
                        }
                        if(progress==50){
                            progressBar.setMessage("Compressing Video, Please wait!!");
                        }
                        if(progress==100){
                            Log.e("i am ","here");
                            progressBar.setProgress(100);
                        }
                        System.out.println("command:onProgress:"+message);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                progressBar.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
//                progressDialog.dismiss();
//                selectVVToLoadVideoInVideoView();
                System.out.println("command:onFinish:");
            }
        });
    }
    public void executeSaveOverlayedVideoCommand() throws FFmpegCommandAlreadyRunningException {

        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";
//        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=10:main_h-overlay_h-10", mFileName};
//        String[] saveMergedVideo ={"-i", path_vv1,
//        "-i", path_vv2,
//        "-i", path_vv2,
//        "-filter_complex", "[0:v]pad=iw*2:ih[int];[int][1:v]overlay=W/2:0[vid]","[0:a:0]","[1:v:0]","[1:a:0]",
//        "-map", "[vid]",
//        "-c:v", "libx264",
//        "-crf", "23",
//        "-preset", "veryfast",
//                mFileName};

//        String[] saveMergedVideo = new String[]{"-i",path_vv1,"-i",path_vv1,"-filter_complex","hstack",mFileName };
//        String[] saveMergedVideo = new String[]{"-i", path_vv1, "-i", path_vv1, "-i", path_vv1, "-i", path_vv1, "-filter_complex", "[0:v][1:v][2:v][3:v]xstack=inputs=4:layout=0_0|w0_0|0_h0|w0_h0[v]", "-map", "[v]",mFileName };
// selected for four video without padding      String[] saveMergedVideo = new String[]{"-i", path_vv1, "-i", path_vv1, "-i", path_vv1, "-i", path_vv1, "-filter_complex", "[0:v][1:v]hstack=inputs=2[top];[2:v][3:v]hstack=inputs=2[bottom];[top][bottom]vstack=inputs=2[v]", "-map", "[v]", mFileName};
          String[] saveMergedVideo = new String[]{"-i", path_vv1, "-i", path_vv3, "-i", path_vv2, "-i", path_vv4, "-filter_complex",
        "[0:v]pad=iw:ih+15[tl];[tl][1:v]vstack,pad=iw+15:ih[l];[2:v]pad=iw:ih+15[tr];[tr][3:v]vstack[r];[l][r]hstack", mFileName};



        final String finalMFileName = mFileName;
        fFmpeg.execute(saveMergedVideo, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Collage created!", Toast.LENGTH_SHORT).show();
                saveBGintoExternalStorage(finalMFileName);
//                selectVVToLoadVideoInVideoView(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                System.out.println("command:onFinish:");
            }
        });
    }
    public void executeRemoveAudiFromVideoCommand(final String path) throws FFmpegCommandAlreadyRunningException {

        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";
          String[] audioRemovedVideo = new String[]{"-y","-i", path, "-c", "copy", "-an", mFileName};



        final String finalMFileName = mFileName;
        fFmpeg.execute(audioRemovedVideo, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Audio Removed!", Toast.LENGTH_SHORT).show();
                selectVVToLoadVideoInVideoView(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                System.out.println("command:onFinish:");
            }
        });
    }

    public void saveBGintoExternalStorage(String collage_path){
        try {
            createDirectory();
            String timeMillis = String.valueOf(new Date().getTime());
            String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            mFileName += "/MergedVideos/"+timeMillis+".mp4";

            int raw_file;
            if(type==1)
                raw_file = R.raw.blue_bg;
            else if(type==2)
                raw_file = R.raw.green_bg;
            else
                raw_file = R.raw.purple_bg;
            InputStream in = getResources().openRawResource(raw_file);
            FileOutputStream out = new FileOutputStream(mFileName);
            byte[] buff = new byte[1024];
            int read = 0;

            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
            executeDecreaseSizeCommand(mFileName, collage_path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
    public void executeDecreaseSizeCommand(final String collage_bg_path, String collage_path) throws FFmpegCommandAlreadyRunningException {

        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
        String logo = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/num"+(selected_vv)+".png";
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";
        System.out.println("::logo::"+logo);
//        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=10:main_h-overlay_h-10", mFileName};
        String[] aspectRatioSelected=new String[]{};
        String p1 = "/storage/emulated/0/Pictures/MergedVideos/test.mp4";
            aspectRatioSelected = new String[]{"-i", collage_path, "-vf", "scale=1000x1750", mFileName};

        final String finalMFileName = mFileName;
        fFmpeg.execute(aspectRatioSelected, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "size decreased!", Toast.LENGTH_SHORT).show();
                try {
                    executeAddBackroundVideoCommand(collage_bg_path, finalMFileName);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
//                selectVVToLoadVideoInVideoView();
                System.out.println("command:onFinish:");
            }
        });
    }
    public void executeAddBackroundVideoCommand(String collage_bg_path, String collage_resized_path) throws FFmpegCommandAlreadyRunningException {

        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";

//        String p1 = "/storage/emulated/0/DCIM/Camera/1580193215064.mp4";
          String p2 = "/storage/emulated/0/Pictures/MergedVideos/blue_bg.mp4";
//          String[] addBg = new String[]{"-i", p2, "-i", p1, "-i", p1, "-i", p1, "-i", p1, "-filter_complex", "$1", mFileName};:overlay_w-main_w-10:overlay_h-main_h-10
//          String[] addBg = new String[]{"-i", p2, "-i", p1, "-filter_complex", "overlay=main_w-overlay_w-40:main_h-overlay_h-40;overlay=x=40:y=40", mFileName};
          String[] addBg = new String[]{"-i", collage_bg_path, "-i", collage_resized_path, "-filter_complex", "overlay=x=40:main_w-overlay_w-40:y=40:main_h-overlay_h-40:x=main_w-overlay_w-40:y=main_h-overlay_h-115", mFileName};
//          String[] addBg = new String[]{"-i", p2, "-i", p1, "-filter_complex", "scale=240x320,overlay=x=main_w-overlay_w-40:y=main_h-overlay_h-40:x=40:y=40", mFileName};



        final String finalMFileName = mFileName;
        fFmpeg.execute(addBg, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "added BG!", Toast.LENGTH_SHORT).show();
//                selectVVToLoadVideoInVideoView(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                System.out.println("command:onFinish:");
            }
        });
    }

    public void createDirectory(){
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/MergedVideos");
        if(!myDir.exists())
            myDir.mkdirs();
    }

    public void storeFileOnSdcard(){

        int[] imgs = new int[]{R.drawable.num1, R.drawable.num2, R.drawable.num3, R.drawable.num4};
        File mFile1 = Environment.getExternalStorageDirectory();
        for(int index=0; index<4; index++){
            String sdPath = mFile1.getAbsolutePath().toString()+"/num"+(index+1)+".png";
            Log.i("hiya", "Your IMAGE ABSOLUTE PATH:-"+sdPath);
            File temp=new File(sdPath);
            if(!temp.exists()){
                Log.e("file","no image file at location :"+sdPath);
                Bitmap bitMap = BitmapFactory.decodeResource(getResources(),imgs[index]);
                String fileName ="num"+(index+1)+".png";
                File mFile2 = new File(mFile1,fileName);
                try {
                    FileOutputStream outStream;
                    outStream = new FileOutputStream(mFile2);
                    bitMap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                String sdPaths = mFile1.getAbsolutePath().toString()+"/"+fileName;
                System.out.println("sdPaths::"+sdPaths);

            }
        }

    }

    public void displayOptions(){
        ll_options.setVisibility(View.VISIBLE);
        iv_swap.setVisibility(View.VISIBLE);
    }

    public void hideOptions(){
        ll_options.setVisibility(View.INVISIBLE);
        iv_swap.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        System.out.println("HelperClass.video_path:"+HelperClass.video_path);
        if(MainActivity.isVideoTrimmed){
            MainActivity.isVideoTrimmed=false;
            selectVVToLoadVideoInVideoView(HelperClass.video_path);
        }
    }

    private String appendVideos()
    {
        try {
//            ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...",true);
            Movie[] inMovies = new Movie[2];

            inMovies[0] = MovieCreator.build(path_vv1);
            inMovies[1] = MovieCreator.build(path_vv2);
//            inMovies[2] = MovieCreator.build(path_vv3);
//            inMovies[3] = MovieCreator.build(path_vv4);

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);

            ////////
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File myDir = new File(root + "/MergedVideos");
            myDir.mkdirs();
            //////////////////
            @SuppressWarnings("resource")
            FileChannel fc = new RandomAccessFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/MergedVideos/wishbyvideo.mp4", "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
//            progressDialog.dismiss();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/wishbyvideo.mp4";
        return mFileName;
    }

}
