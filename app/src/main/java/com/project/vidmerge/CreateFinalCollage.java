package com.project.vidmerge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.project.vidmerge.Utils.ProgressCalculator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

public class CreateFinalCollage extends AppCompatActivity {

    FFmpeg fFmpeg;
    String path_vv1, path_vv2, path_vv3, path_vv4;
    int type;
    ProgressDialog progressDialog;
    boolean isCollageCreated=false;
    ImageView iv_bg;
    VideoView vv;
    String createdCollageVideoPath="";
    ProgressCalculator mProgressCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_final_collage);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(getIntent()!=null){
            path_vv1 = getIntent().getStringExtra("PATH1");
            path_vv2 = getIntent().getStringExtra("PATH2");
            path_vv3 = getIntent().getStringExtra("PATH3");
            path_vv4 = getIntent().getStringExtra("PATH4");
            type = getIntent().getIntExtra("TYPE",1);
        }
        try {
            loadFFmpegLib();
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }

        vv = findViewById(R.id.vv1);
        iv_bg = findViewById(R.id.iv_bg);
        int[] bg_images = new int[]{R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3};

//        iv_bg.setImageResource(bg_images[new Random().nextInt(3)]);

        TextView tv_share = findViewById(R.id.tv_share);
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCollageCreated){

                    String path = createdCollageVideoPath; //should be local path of downloaded video

                    MediaScannerConnection.scanFile(getApplicationContext(), new String[] { path },

                            null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Intent shareIntent = new Intent(
                                            android.content.Intent.ACTION_SEND);
                                    shareIntent.setType("video/*");
                                    shareIntent.putExtra(
                                            android.content.Intent.EXTRA_SUBJECT, "");
                                    shareIntent.putExtra(
                                            android.content.Intent.EXTRA_TITLE, "");
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    shareIntent
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                    startActivity(Intent.createChooser(shareIntent, "String"));

                                }
                            });

//                    ContentValues content = new ContentValues(4);
//                    content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
//                            System.currentTimeMillis() / 1000);
//                    content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
//                    content.put(MediaStore.Video.Media.DATA, path);
//
//                    ContentResolver resolver = getApplicationContext().getContentResolver();
//                    Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);
//
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("video/*");
////                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey this is the video subject");
////                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey this is the video text");
//                    sharingIntent.putExtra(Intent.EXTRA_STREAM,uri);
//                    startActivity(Intent.createChooser(sharingIntent,"Share Video"));

//                    File videoFile = new File(createdCollageVideoPath);
//                    Uri videoURI = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
//                            ? FileProvider.getUriForFile(CreateFinalCollage.this, CreateFinalCollage.this.getPackageName(), videoFile)
//                            : Uri.fromFile(videoFile);
//                    ShareCompat.IntentBuilder.from(CreateFinalCollage.this)
//                            .setStream(videoURI)
//                            .setType("video/mp4")
//                            .setChooserTitle("Share video...")
//                            .startChooser();
                }
            }
        });
    }

    public void loadFFmpegLib() throws FFmpegNotSupportedException {
        if(fFmpeg==null){
            fFmpeg = FFmpeg.getInstance(CreateFinalCollage.this);

            fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                    try {
//                        progressDialog = ProgressDialog.show(CreateFinalCollage.this, "", "Making Video Collage. Please wait...", true);
                        progressDialog = new ProgressDialog(CreateFinalCollage.this);
                        progressDialog.setCancelable(false);//you can cancel it by pressing back button
                        progressDialog.setMessage("Making Video Collage...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setProgress(0);//initially progress is 0
                        progressDialog.setMax(100);//sets the maximum value 100
                        progressDialog.show();//displays the progress bar
                        executeSaveOverlayedVideoCommand();
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
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

    public void createDirectory(){
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/MergedVideos");
        if(!myDir.exists())
            myDir.mkdirs();
    }

    public void executeSaveOverlayedVideoCommand() throws FFmpegCommandAlreadyRunningException {

//        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
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



        mProgressCalculator = new ProgressCalculator();
        final String finalMFileName = mFileName;
        fFmpeg.execute(saveMergedVideo, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
//                progressDialog.dismiss();
//                Toast.makeText(MainActivity.this, "Collage created!", Toast.LENGTH_SHORT).show();
                saveBGintoExternalStorage(finalMFileName);
//                selectVVToLoadVideoInVideoView(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                int progress = mProgressCalculator.calcProgress(message);
                Log.e("VideoCronProgress == ", progress + "..");
                if (progress != 0 && progress <= 100) {
                    if (progress >= 99) {
                        progress = 100;
                    }
                    if(progress<=50)
                        progressDialog.setProgress(progress);
                    System.out.println("pro_progress"+progress+"%%");
//                    listener.onProgress(progress);
                }
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
//                progressDialog.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
//                progressDialog.dismiss();
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

//        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
//        String logo = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/num"+(selected_vv)+".png";
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";
//        System.out.println("::logo::"+logo);
//        String[] addWatermark ={"-i", preFilePath, "-i", logo, "-filter_complex", "overlay=10:main_h-overlay_h-10", mFileName};
        String[] aspectRatioSelected=new String[]{};
        String p1 = "/storage/emulated/0/Pictures/MergedVideos/test.mp4";
        aspectRatioSelected = new String[]{"-i", collage_path, "-vf", "scale=1000x1750", mFileName};

        final String finalMFileName = mFileName;
        fFmpeg.execute(aspectRatioSelected, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
//                progressDialog.dismiss();
//                Toast.makeText(MainActivity.this, "size decreased!", Toast.LENGTH_SHORT).show();
                try {
                    executeAddBackroundVideoCommand(collage_bg_path, finalMFileName);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                int progress = mProgressCalculator.calcProgress(message);
                Log.e("VideoCronProgress == ", progress + "..");
                if (progress != 0 && progress <= 100) {
                    if (progress >= 99) {
                        progress = 100;
                    }
                    if(progress>50 && progress<=60)
                        progressDialog.setProgress(progress);
                    System.out.println("pro_progress"+progress+"%%");
//                    listener.onProgress(progress);
                }
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
//                progressDialog.dismiss();
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

    public void executeAddBackroundVideoCommand(String collage_bg_path, String collage_resized_path) throws FFmpegCommandAlreadyRunningException {

//        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        createDirectory();
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/ViralityVideosCollage");
//        if(!myDir.exists())
//            myDir.mkdirs();
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mFileName += "/MergedVideos/"+timeMillis+".mp4";

//        String p1 = "/storage/emulated/0/DCIM/Camera/1580193215064.mp4";
        String p2 = "/storage/emulated/0/Pictures/MergedVideos/blue_bg.mp4";
//          String[] addBg = new String[]{"-i", p2, "-i", p1, "-i", p1, "-i", p1, "-i", p1, "-filter_complex", "$1", mFileName};:overlay_w-main_w-10:overlay_h-main_h-10
//          String[] addBg = new String[]{"-i", p2, "-i", p1, "-filter_complex", "overlay=main_w-overlay_w-40:main_h-overlay_h-40;overlay=x=40:y=40", mFileName};
        String[] addBg = new String[]{"-i", collage_bg_path, "-i", collage_resized_path, "-filter_complex", "overlay=x=40:main_w-overlay_w-40:y=40:main_h-overlay_h-40:x=main_w-overlay_w-40:y=main_h-overlay_h-115", mFileName};
//          String[] addBg = new String[]{"-i", p2, "-i", p1, "-filter_complex", "scale=240x320,overlay=x=main_w-overlay_w-40:y=main_h-overlay_h-40:x=40:y=40", mFileName};
//        String[] addBg = new String[]{"-i", collage_bg_path, "-i", collage_resized_path,"-filter_complex", "[1:v]setpts=PTS-10/TB[a];[0:v][a]overlay=enable=gte(t,5):shortest=1[out]","-map", "[out]", "-map", "0:a","-c:v", "libx264", "-crf", "18", "-pix_fmt", "yuv420p","-c:a", "copy", mFileName};



        final String finalMFileName = mFileName;
        fFmpeg.execute(addBg, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                saveLogoIntoExternalStorage(finalMFileName);
//                progressDialog.dismiss();
//                Toast.makeText(CreateFinalCollage.this, "Video Collage created successfully!", Toast.LENGTH_SHORT).show();
//                isCollageCreated=true;
//                loadVideo(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                int progress = mProgressCalculator.calcProgress(message);
                if (progress != 0 && progress <= 100) {
                    if (progress >= 99) {
                        progress = 99;
                    }
                    if(progress>60 && progress<=90)
                        progressDialog.setProgress(progress);
                    System.out.println("pro_progress"+progress+"%%");
//                    listener.onProgress(progress);
                }
                System.out.println("command:onProgress:"+message);
            }

            @Override
            public void onFailure(String message) {
//                progressDialog.dismiss();
                System.out.println("command:Failure:"+message);
            }

            @Override
            public void onStart() {
                System.out.println("command:onStart:");
            }

            @Override
            public void onFinish() {
//                progressDialog.dismiss();
                System.out.println("command:onFinish:");
            }
        });
    }

    public void saveLogoIntoExternalStorage(String collage_path){
        try {
            createDirectory();
            String timeMillis = String.valueOf(new Date().getTime());
            String mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            mFileName += "/MergedVideos/"+timeMillis+".mp4";

            int raw_file;
            raw_file = R.raw.logo;
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
            executeAddLogoAtCenterCommand(mFileName, collage_path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public void executeAddLogoAtCenterCommand(String logo_path, String collage_resized_path) throws FFmpegCommandAlreadyRunningException {

//        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
//        createDirectory();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/ViralityVideosCollage");
        if(!myDir.exists())
            myDir.mkdirs();
        String timeMillis = String.valueOf(new Date().getTime());
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/ViralityVideosCollage/"+timeMillis+".mp4";


//        String logo_path = storeFileOnSdcard();
        String[] addBg = new String[]{"-i", collage_resized_path, "-i", logo_path, "-filter_complex", "overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2", mFileName};


        final String finalMFileName = mFileName;
        fFmpeg.execute(addBg, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(CreateFinalCollage.this, "Video Collage created successfully!", Toast.LENGTH_SHORT).show();
                isCollageCreated=true;
                loadVideo(finalMFileName);
                System.out.println("command:Success:"+message);
            }

            @Override
            public void onProgress(String message) {
                int progress = mProgressCalculator.calcProgress(message);
                Log.e("VideoCronProgress == ", progress + "..");
                if (progress != 0 && progress <= 100) {
                    if (progress >= 99) {
                        progress = 99;
                    }
                    if(progress>90)
                        progressDialog.setProgress(progress);
                    System.out.println("pro_progress"+progress+"%%");
//                    listener.onProgress(progress);
                }
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

    public void loadVideo(String path){
        createdCollageVideoPath = path;
        vv.setVideoURI(Uri.fromFile(new File(path)));

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                System.out.println("setOnPreparedListener:::");
                vv.start();
//                mp1 = mp;
            }
        });

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/MergedVideos");
        deleteDirectory(myDir);

    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

}
